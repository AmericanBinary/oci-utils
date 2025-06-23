package oci_utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import oci_utils.model.*;

import java.nio.charset.StandardCharsets;
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

    public Config.Profile getOrLoadDefailtProfile() {
        return getOrLoadConfig().getDefaultProfile(getCliConfig());
    }

    @SneakyThrows
    public List<NamedOciEntity<CompartmentListItem>> listCompartments() {
        // oci iam compartment list --compartment-id ${tenancy_id} --name ${compartment} | jq -r .data[].id
        var result = run("oci iam compartment list --compartment-id " + getOrLoadDefailtProfile().getTenancy());
        var compartments = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<CompartmentListItem>>() {
        });
        return compartments.getData().stream().map(e -> new NamedOciEntity<CompartmentListItem>().setEntity(e)).toList();
    }

    @SneakyThrows
    public CompartmentListItem getCompartment(String name) {
        String tenancy = getOrLoadDefailtProfile().getTenancy();
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
        var mysqlClusters = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<MysqlClusterListItem>>() {
        });
        if (mysqlClusters.getData().size() != 1) {
            throw new IllegalStateException("Expected only one mysql cluster in list but found " + mysqlClusters.getData().size() + ": " + mysqlClusters.getData());
        }
        return mysqlClusters.getData().getFirst();
    }

    @SneakyThrows
    public MysqlClusterListItem getCompartmentIdMysqlCluster(String compartmentId, String clusterName) {
        var result = run("oci mysql db-system list --compartment-id " + compartmentId + " --name " + clusterName);
        var mysqlClusters = mapper.readValue(result.output(), new TypeReference<BaseOciDataList<MysqlClusterListItem>>() {
        });
        if (mysqlClusters.getData().size() != 1) {
            throw new IllegalStateException("Expected only one mysql cluster in list but found " + mysqlClusters.getData().size() + ": " + mysqlClusters.getData());
        }
        return mysqlClusters.getData().getFirst();
    }

    @SneakyThrows
    public SessionItem getSession(String sessionId) {
        var result = run("oci bastion session get --session-id " + sessionId);
        return mapper.readValue(result.output(), new TypeReference<BaseOciDataItem<SessionItem>>() {
        }).getData();
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
