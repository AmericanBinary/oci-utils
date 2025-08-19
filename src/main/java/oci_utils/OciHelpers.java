package oci_utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import oci_utils.model.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@Slf4j
public class OciHelpers {
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
        if (compartments.getData().size() != 1) {
            throw new IllegalStateException("Expected one compartment in list but found " + compartments.getData().size() + ": " + compartments.getData());
        }
        return compartments.getData().getFirst();
    }

    @SneakyThrows
    public BastionListItem getCompartmentOnlyBastion(String compartmentName) {
        String compartmentId = getCompartment(compartmentName).getId();
        return getCompartmentIdOnlyBastion(compartmentId);
    }

    @SneakyThrows
    public BastionListItem getCompartmentIdOnlyBastion(String compartmentId) {
        var result = run("oci bastion bastion list --compartment-id " + compartmentId + " --all"); // also supports --name
        var bastions = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<BastionListItem>>() {
        });
        if (bastions.getData().size() != 1) {
            throw new IllegalStateException("Expected one bastion in list but found " + bastions.getData().size() + ": " + bastions.getData());
        }
        return bastions.getData().getFirst();
    }

    @SneakyThrows
    public BastionListItem getCompartmentIdBastion(String compartmentId, String bastionName) {
        var result = run("oci bastion bastion list --compartment-id " + compartmentId + " --name " + bastionName);
        var bastions = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<BastionListItem>>() {
        });
        if (bastions.getData().size() != 1) {
            throw new IllegalStateException("Expected one bastion in list but found " + bastions.getData().size() + ": " + bastions.getData());
        }
        return bastions.getData().getFirst();
    }

    // cluster_info="$(oci ce cluster list --compartment-id ${compartment_id} --name ${cluster_name} | jq -c .)"
    @SneakyThrows
    public OkeClusterListItem getCompartmentIdOnlyOkeCluster(String compartmentId) {
        var result = run("oci ce cluster list --compartment-id " + compartmentId); // also supports --name
        var bastions = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<OkeClusterListItem>>() {
        });
        if (bastions.getData().size() != 1) {
            throw new IllegalStateException("Expected one bastion in list but found " + bastions.getData().size() + ": " + bastions.getData());
        }
        return bastions.getData().getFirst();
    }

    @SneakyThrows
    public OkeClusterListItem getCompartmentIdOkeCluster(String compartmentId, String clusterName) {
        var result = run("oci ce cluster list --compartment-id " + compartmentId + " --name " + clusterName);
        var bastions = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<OkeClusterListItem>>() {
        });
        if (bastions.getData().size() != 1) {
            throw new IllegalStateException("Expected one bastion in list but found " + bastions.getData().size() + ": " + bastions.getData());
        }
        return bastions.getData().getFirst();
    }

    @SneakyThrows
    public OkeClusterListItem getCompartmentIdOkeClusterById(String clusterId) {
        var result = run("oci ce cluster get --cluster-id " + clusterId);
        var item = mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<OkeClusterListItem>>() {});
        return item.getData();
    }

    @SneakyThrows
    public SessionItem createPortForwardingSession(String bastionId, String sshPublicKeyFile, String host, int port) {
        // oci bastion session create-port-forwarding --bastion-id ${bastion_id} --ssh-public-key-file ${ssh_public_key} --target-private-ip ${private_endpoint_host} --target-port ${private_endpoint_port} | tee /dev/stderr | jq -r .data.id
        var result = run("oci bastion session create-port-forwarding --bastion-id " + bastionId + " --ssh-public-key-file " + sshPublicKeyFile + " --target-private-ip " + host + " --target-port " + port);
        var session = mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<SessionItem>>() {
        });
        return session.getData();
    }

    @SneakyThrows
    public MysqlClusterListItem getCompartmentIdOnlyMysqlCluster(String compartmentId) {
        var result = run("oci mysql db-system list --compartment-id " + compartmentId);
        return getMysqlClusterListItem(result);
    }

    @SneakyThrows
    public MysqlClusterListItem getCompartmentIdMysqlClusterByName(String compartmentId, String clusterName) {
        var result = run("oci mysql db-system list --compartment-id " + compartmentId + " --display-name " + clusterName);
        return getMysqlClusterListItem(result);
    }

    private MysqlClusterListItem getMysqlClusterListItem(RunResult result) throws JsonProcessingException {
        var mysqlClusters = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<MysqlClusterListItem>>() {
        });
        if (mysqlClusters.getData().size() != 1) {
            throw new IllegalStateException("Expected only one mysql cluster in 'oci mysql db-system list' output but found " + mysqlClusters.getData().size() + ": " + mysqlClusters.getData());
        }
        return mysqlClusters.getData().getFirst();
    }

    @SneakyThrows
    public MysqlClusterListItem getCompartmentIdMysqlClusterById(String id) {
        var result = run("oci mysql db-system get --db-system-id " + id);
        var item = mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<MysqlClusterListItem>>() {});
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


    record RunResult(String output, String error, int code) {
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @Accessors(chain = true)
    public static class RunResultError extends RuntimeException {
        RunResult runResult;
    }
}
