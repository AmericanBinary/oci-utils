package oci_utils;

import lombok.Data;
import lombok.experimental.Accessors;
import oci_utils.model.Config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class OciHelpersConfig {
    static final Path DEFAULT_PATH = Paths.get(System.getProperty("user.home"), ".oci", "config");
    static final Path USER_PATH = Optional.ofNullable(System.getenv("OCI_CLI_CONFIG_FILE")).map(Path::of).orElse(null);
    static final String DEFAULT_PROFILE = "DEFAULT";
    static final String USER_PROFILE = System.getenv("OCI_CLI_PROFILE");

    Path configPath = Optional.ofNullable(USER_PATH).orElse(DEFAULT_PATH);
    String profile = Optional.ofNullable(USER_PROFILE).orElse(DEFAULT_PROFILE);
    Config config;
}
