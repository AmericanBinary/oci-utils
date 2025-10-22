package oci_utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import oci_utils.model.*;
import org.ini4j.Ini;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class OciHelpers {
    static final RetryRegistry RETRY_CONFIG = RetryRegistry.of(RetryConfig.custom()
            .maxAttempts(10)
            .waitDuration(Duration.ofSeconds(1))
            .retryOnException(ignored -> true)
            .build());
    @Setter(AccessLevel.NONE)
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    @Setter(AccessLevel.NONE)
    OciHelpersConfig cliConfig = new OciHelpersConfig();

    @SneakyThrows
    public Config loadLocalConfig() {
        var config = cliConfig.getConfigPath();
        log.trace("loading local config from: {}", config);

        var ini = new Ini(config.toFile());
        log.trace("config: {} => ini: {}", config, ini);

        return mapper.convertValue(ini, Config.class);
    }

    private Config getOrLoadConfig() {
        var c = cliConfig.getConfig();
        if (c != null) return c;
        Config loaded = loadLocalConfig();
        cliConfig.setConfig(loaded);
        return loaded;
    }

    public Config.Profile getOrLoadDefaultProfile() {
        return getOrLoadConfig().getDefaultProfile(getCliConfig());
    }

    @SneakyThrows
    public List<NamedOciEntity<CompartmentListItem>> listCompartments() {
        // oci iam compartment list --compartment-id ${tenancy_id} --name ${compartment} | jq -r .data[].id
        var result = run("oci iam compartment list --compartment-id " + getOrLoadDefaultProfile().getTenancy());
        var compartments = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<CompartmentListItem>>() {
        });
        return compartments.getData().stream().map(e -> new NamedOciEntity<CompartmentListItem>().setEntity(e)).toList();
    }

    @SneakyThrows
    public CompartmentListItem getCompartment(String name) {
        String tenancy = getOrLoadDefaultProfile().getTenancy();
        var result = run("oci iam compartment list --compartment-id " + tenancy + " --name " + name);
        var compartments = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<CompartmentListItem>>() {
        });
        return one(compartments);
    }

    @SneakyThrows
    public BaseOciDataList<BastionListItem> listBastionInCompartment(String compartmentId) {
        var result = run("oci bastion bastion list --compartment-id " + compartmentId + " --all");
        return mapper.readValue(result.output(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    public BastionListItem getBastionInCompartment(String compartmentId, String name) {
        if (name == null) {
            one(listBastionInCompartment(compartmentId));
        }

        var result = run("oci bastion bastion list --compartment-id " + compartmentId + " --name " + name);
        // noinspection Convert2Diamond
        return one(mapper.readValue(result.output(), new TypeReference<BaseOciDataList<BastionListItem>>() {
        }));
    }

    @SneakyThrows
    public BaseOciDataList<OkeClusterListItem> listOkeClusterInCompartment(String compartmentId) {
        return mapper.readValue(run("oci ce cluster list --compartment-id " + compartmentId).output(), new TypeReference<>() {
        });
    }

    // cluster_info="$(oci ce cluster list --compartment-id ${compartment_id} --name ${cluster_name} | jq -c .)"
    @SneakyThrows
    public OkeClusterListItem getOkeClusterInCompartment(String compartmentId, String name) {
        if (name == null) {
            return one(listOkeClusterInCompartment(compartmentId));
        }

        // noinspection Convert2Diamond
        return one(mapper.readValue(run("oci ce cluster list --compartment-id " + compartmentId + " --name " + name).output(), new TypeReference<BaseOciDataList<OkeClusterListItem>>() {
        }));
    }

    @SneakyThrows
    public OkeClusterListItem getCompartmentIdOkeClusterById(String clusterId) {
        var result = run("oci ce cluster get --cluster-id " + clusterId);
        var item = mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<OkeClusterListItem>>() {
        });
        return item.getData();
    }

    @SneakyThrows
    public SessionItem createPortForwardingSession(String bastionId, String sshPublicKeyFile, LocalPortForward forward) {
        // oci bastion session create-port-forwarding --bastion-id ${bastion_id} --ssh-public-key-file ${ssh_public_key} --target-private-ip ${private_endpoint_host} --target-port ${private_endpoint_port} | tee /dev/stderr | jq -r .data.id
        var result = run("oci bastion session create-port-forwarding --bastion-id " + bastionId + " --ssh-public-key-file " + sshPublicKeyFile + " --target-private-ip " + forward.remoteHost() + " --target-port " + forward.remotePort());
        var session = mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<SessionItem>>() {
        });
        return session.getData();
    }

    @SneakyThrows
    public SessionItem getAndWaitForSession(BastionListItem bastion, String keyFile, LocalPortForward portForward) {
        return waitForPortForwardingSession(bastion, portForward, createPortForwardingSession(bastion.getId(), keyFile, portForward));
    }

    @SneakyThrows
    public SessionItem waitForPortForwardingSession(BastionListItem bastion, LocalPortForward forward, SessionItem session) {
        if (session.getSshMetadata() == null) {
            log.debug("ssh metadata is being retried for session {} (bastion {} for forward: {})", session.getId(), bastion.getName(), forward);
            SessionItem.SshMetadata sessionSsh = RETRY_CONFIG.retry("sshMetadata")
                    .executeCallable(() -> Optional.of(getSession(session.getId())).map(SessionItem::getSshMetadata).orElseThrow());
            session.setSshMetadata(sessionSsh);
        }
        return session;
    }

    @SneakyThrows
    public Process startSession(SessionItem session, LocalPortForward portForward) {
        return RETRY_CONFIG.retry("startSession")
                .executeCallable(() -> {
                    long start = System.nanoTime();
                    var pb = new ProcessBuilder(("ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -N -L 127.0.0.1:" + portForward.localPort() + ":" + portForward.remoteHost() + ":" + portForward.remotePort() + " -p 22 " + session.getId() + "@host.bastion." + getOrLoadDefaultProfile().getRegion() + ".oci.oraclecloud.com").split(" "));

                    if (log.isInfoEnabled())
                        pb.inheritIO();
                    else
                        pb.redirectOutput(ProcessBuilder.Redirect.PIPE)
                                .redirectError(ProcessBuilder.Redirect.PIPE);

                    Process process = pb
                            .start();

                    boolean exited = process.waitFor(10, TimeUnit.SECONDS);
                    if (exited) {
                        Duration duration = Duration.ofNanos(System.nanoTime() - start);
                        log.debug("attempt failed, ssh process lasted {} which is less than 10 seconds, we should probably try again", duration);
                        throw new RuntimeException("ssh process died too fast - try running with -vv for increased verbosity");
                    } else {
                        log.info("ssh process lasted {} and still alive so this is successful", Duration.ofNanos(System.nanoTime() - start));
                    }
                    return process;
                });
    }

    @SneakyThrows
    public BaseOciDataList<MysqlClusterListItem> listMysqlInCompartment(String compartmentId) {
        var result = run("oci mysql db-system list --compartment-id " + compartmentId);
        return mapper.readValue(result.output(), new TypeReference<>() {
        });
    }

    @SneakyThrows
    public MysqlClusterListItem getMysqlInCompartment(String compartmentId, String name) {
        if (name == null) {
            return one(listMysqlInCompartment(compartmentId));
        }

        var result = run("oci mysql db-system list --compartment-id " + compartmentId + " --display-name " + name);
        // noinspection Convert2Diamond
        return one(mapper.readValue(result.output(), new TypeReference<BaseOciDataList<MysqlClusterListItem>>() {
        }));
    }

    @SneakyThrows
    public MysqlClusterListItem getMysqlById(String id) {
        var result = run("oci mysql db-system get --db-system-id " + id);
        var item = mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<MysqlClusterListItem>>() {
        });
        return item.getData();
    }

    @SneakyThrows
    public SessionItem getSession(String sessionId) {
        var result = run("oci bastion session get --session-id " + sessionId);
        return mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<SessionItem>>() {
        }).getData();
    }

    @SneakyThrows
    public void configureLocalhostContext(OkeClusterListItem cluster, File file) {
        // oci ce cluster create-kubeconfig --cluster-id ${cluster_id} --file ~/.kube/config --region us-sanjose-1 --token-version 2.0.0  --kube-endpoint PRIVATE_ENDPOINT
        // kubectl config rename-context $(kubectl config current-context) ${cluster_name}
        // cluster_config_id=$(kubectl config view -o json | jq '.contexts[] | { name, cluster: .context.cluster} | select(.name == "'${cluster_name}'") | .cluster' -r)
        // kubectl config set-cluster ${cluster_config_id} --server https://127.0.0.1:6443

        var kubeConfig = file;
        if (kubeConfig == null) {
            String fromEnv = System.getenv("KUBECONFIG");
            if (fromEnv != null) {
                kubeConfig = new File(fromEnv);
            }
        }
        if (kubeConfig == null) {
            kubeConfig = Path.of(System.getProperty("user.home"), ".kube", "config").toFile();
        }

        run("oci ce cluster create-kubeconfig --cluster-id " + cluster.getId() +
                " --file " + kubeConfig.getPath() +
                " --region " + getOrLoadDefaultProfile().getRegion() +
                " --token-version 2.0.0 --kube-endpoint PRIVATE_ENDPOINT");

        // todo manually activate and rename maybe
        var old = run("kubectl config current-context").output().trim();

        boolean alreadyHaveContext = listFromIterator(mapper.readTree(run("kubectl config view -o json").output()).get("contexts").elements())
                .stream().anyMatch(e -> cluster.getName().equals(e.get("name").asText()));
        if (alreadyHaveContext) {
            log.warn("context already exists, clearing for new one: {}", cluster.getName());
            run("kubectl config delete-context " + cluster.getName());
        }

        run("kubectl config rename-context " + old + " " + cluster.getName());

        var clusterConfigId = listFromIterator(mapper.readTree(run("kubectl config view -o json").output()).get("contexts").elements())
                .stream()
                .map(e -> mapper.createObjectNode()
                        .put("name", e.get("name").asText())
                        .put("cluster", e.path("context").path("cluster").asText()))
                .filter(e -> cluster.getName().equals(e.get("name").asText()))
                .map(e -> e.get("cluster").asText())
                .findAny().orElseThrow();

        run("kubectl config set-cluster " + clusterConfigId + " --server https://127.0.0.1:6443");
    }

    <T> List<T> listFromIterator(Iterator<T> iterator) {
        var t = new ArrayList<T>();
        iterator.forEachRemaining(t::add);
        return t;
    }

    @SneakyThrows
    RunResult run(String command) {
        log.trace("running command {}", command);
        var pb = new ProcessBuilder(command.split("\\s+"));
        var p = pb.start();
        var exit = p.waitFor();
        var dataInput = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        var dataError = new String(p.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        RunResult runResult = new RunResult(dataInput, dataError, exit);
        log.trace("running command {} resulted in {}", command, runResult);
        if (exit != 0) {
            throw new RunResultError().setRunResult(runResult);
        }
        return runResult;
    }

    <T extends BaseOciEntity> T one(BaseOciDataList<T> list) {
        if (list.getData().size() != 1) {
            List<String> names = list.getData().stream().map(BaseOciEntity::getName).toList();
            throw new MultipleResultError(names);
        }
        return list.getData().getFirst();
    }

    public record LocalPortForward(int localPort, String remoteHost, int remotePort) {
    }

    record RunResult(String output, String error, int code) {
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @Accessors(chain = true)
    public static class RunResultError extends RuntimeException {
        RunResult runResult;
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @Accessors(chain = true)
    public static class MultipleResultError extends RuntimeException {
        final List<String> names;

        @Override
        public String getMessage() {
            return "Expected one item but found these instead: " + String.join(", ", names);
        }
    }
}
