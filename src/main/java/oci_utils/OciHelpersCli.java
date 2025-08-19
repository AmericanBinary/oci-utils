package oci_utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oci_utils.model.BastionListItem;
import oci_utils.model.MysqlClusterListItem;
import oci_utils.model.OkeClusterListItem;
import oci_utils.model.SessionItem;
import org.springframework.util.Assert;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Paths;

@Slf4j
@CommandLine.Command(
        name = "oci-utils",
        description = "",
        versionProvider = OciHelpersCli.ManifestVersionProvider.class,
        mixinStandardHelpOptions = true,
        sortOptions = false,
        scope = CommandLine.ScopeType.INHERIT,
        subcommands = {
                OciHelpersCli.BastionUtils.class,
                OciHelpersCli.KubectlUtils.class,
                OciHelpersCli.Utils.class,
                AutoComplete.GenerateCompletion.class,
        }
)
class OciHelpersCli implements Runnable {
    static final OciHelpers INSTANCE = new OciHelpers();
    static final String HOME_SSH_ID_RSA_PUB = Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa.pub").toString();

    @CommandLine.Mixin
    LogbackVerbosityMixin verbosityMixin;

    public static void main(String[] args) {
        System.exit(new CommandLine(new OciHelpersCli()).execute(args));
    }

    @Override
    public void run() {
        log.info("Verbosity mixin: {}", verbosityMixin);
    }

    @CommandLine.Command(name = "util", aliases = "u", description = "general utilities", subcommands = {
            Utils.Compartments.class,
            Utils.Config.class,
    })
    static class Utils {
        @CommandLine.Command(name = "compartments", aliases = {"co", "comp"})
        static class Compartments {
            @SneakyThrows
            @CommandLine.Command(name = "list", aliases = "l", description = "list compartments in tenancy")
            void print() {
                System.out.println(INSTANCE.mapper.writeValueAsString(INSTANCE.listCompartments()));
            }

            @SneakyThrows
            @CommandLine.Command(name = "get", aliases = "g", description = "get compartment")
            void get(@CommandLine.Option(names = {"-n", "--name"}, required = true) String name) {
                System.out.println(INSTANCE.mapper.writeValueAsString(INSTANCE.getCompartment(name)));
            }
        }

        @CommandLine.Command(name = "config", aliases = "c")
        static class Config {
            @SneakyThrows
            @CommandLine.Command(name = "print", aliases = "p", description = "print configuration")
            void print() {
                var config = INSTANCE.loadLocalConfig();
                log.info("Configuration: {}", config);
                System.out.println(INSTANCE.mapper.writeValueAsString(config));
            }
        }
    }

    @CommandLine.Command(name = "bastion-utils", aliases = "bu", description = "bastion connection utilities")
    static class BastionUtils {
        @CommandLine.Command(name = "forward-kubectl")
        @SneakyThrows
        void forwardKubectlPort(
                @CommandLine.Option(names = {"-c", "--compartment"}, required = true) String compartment,
                @CommandLine.Option(names = {"-b", "--bastion-name"}) String bastionName,
                @CommandLine.Option(names = {"-k", "--cluster-name"}) String clusterName
        ) {
            var c = INSTANCE.getCompartment(compartment);
            BastionListItem bastion = (
                    bastionName != null
                            ? INSTANCE.getCompartmentIdBastion(c.getId(), bastionName)
                            : INSTANCE.getCompartmentIdOnlyBastion(c.getId())
            );
            OkeClusterListItem cluster = (
                    clusterName != null
                            ? INSTANCE.getCompartmentIdOkeCluster(c.getId(), clusterName)
                            : INSTANCE.getCompartmentIdOnlyOkeCluster(c.getId())
            );

            String privateEndpoint = cluster.getEndpoints().getPrivateEndpoint();
            Assert.notNull(privateEndpoint, "Must have private endpoint on cluster to forward to private endpoint");
            var host = privateEndpoint.split(":")[0];
            var port = Integer.parseInt(privateEndpoint.split(":")[1]);

            var session = INSTANCE.getAndWaitForSession(bastion, HOME_SSH_ID_RSA_PUB, host, port);
            printSession(session, port, host);
            INSTANCE.startSession(session, host, port).waitFor();
        }

        @CommandLine.Command(name = "forward-mysql")
        @SneakyThrows
        void forwardMysqlPort(
                @CommandLine.Option(names = {"-c", "--compartment"}, required = true) String compartment,
                @CommandLine.Option(names = {"-b", "--bastion-name"}) String bastionName,
                @CommandLine.Option(names = {"-d", "-m", "--database-name", "--mysql-database-name"}, description = "precedence over --database-id") String dbName,
                @CommandLine.Option(names = {"-di", "--database-id", "--mysql-database-id"}) String dbId
        ) {
            var c = INSTANCE.getCompartment(compartment);
            BastionListItem bastion = (
                    bastionName != null
                            ? INSTANCE.getCompartmentIdBastion(c.getId(), bastionName)
                            : INSTANCE.getCompartmentIdOnlyBastion(c.getId())
            );
            MysqlClusterListItem cluster = (
                    dbName != null
                            ? INSTANCE.getCompartmentIdMysqlClusterByName(c.getId(), dbName)
                            : (
                                    dbId != null
                                            ? INSTANCE.getCompartmentIdMysqlClusterById(dbId)
                                            : INSTANCE.getCompartmentIdOnlyMysqlCluster(c.getId())
                            )
            );

            var host = cluster.getEndpoints().getFirst().getIpAddress();
            var port = cluster.getEndpoints().getFirst().getPort();

            var session = INSTANCE.getAndWaitForSession(bastion, HOME_SSH_ID_RSA_PUB, host, port);
            printSession(session, port, host);
            INSTANCE.startSession(session, host, port).waitFor();
        }

        private void printSession(SessionItem session, int port, String host) throws JsonProcessingException {
            if (log.isInfoEnabled()) {
                log.info("session JSON from oci bastion server api is: {}", INSTANCE.mapper.writeValueAsString(session.getSshMetadata()));
                String s = "ssh -N -L 127.0.0.1:" + port + ":" + host + ":" + port + " -p 22 " + session.getId() + "@host.bastion." + INSTANCE.getOrLoadDefaultProfile().getRegion() + ".oci.oraclecloud.com";
                log.info("session ssh command from oci bastion server api is: {}", s);
            }
        }
    }

    @CommandLine.Command(name = "kubectl-utils", aliases = "ku", description = "kubectl utilities")
    static class KubectlUtils {
        @CommandLine.Command(name = "configure-localhost-context", aliases = "clc")
        @SneakyThrows
        void configureLocalhostContext(
                @CommandLine.Option(names = {"-c", "--compartment"}, required = true) String compartment,
                @CommandLine.Option(names = {"-k", "--cluster-name"}) String clusterName,
                @CommandLine.Option(names = {"-ki", "--cluster-id"}) String clusterId,
                @CommandLine.Option(names = {"-f", "--config-file"}, description = "defaults to ${KUBECONFIG:-~/.kube/config}") File file
        ) {
            OkeClusterListItem cluster;
            if (clusterId != null) {
                cluster = INSTANCE.getCompartmentIdOkeClusterById(clusterId);
            } else {
                var c = INSTANCE.getCompartment(compartment);
                cluster = (
                        clusterName != null
                                ? INSTANCE.getCompartmentIdOkeCluster(c.getId(), clusterName)
                                : INSTANCE.getCompartmentIdOnlyOkeCluster(c.getId())
                );
            }

            INSTANCE.configureLocalhostContext(cluster, file);
            log.info("created context {} (for cluster id '{}')", cluster.getName(), cluster.getId());
        }
    }

    static class ManifestVersionProvider implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() {
            return new String[]{getClass().getPackage().getImplementationVersion()};
        }
    }
}
