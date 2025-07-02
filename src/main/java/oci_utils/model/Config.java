package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import oci_utils.OciHelpersConfig;

import java.nio.file.Path;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Config {
    @JsonIgnore
    @JsonAnyGetter
    @JsonAnySetter
    Map<String, Profile> profiles;

    public Profile getDefaultProfile(OciHelpersConfig config) {
        return profiles == null ? null : profiles.get(config.getProfile());
    }

    @Data
    @Accessors(chain = true)
    public static class Profile {
        @JsonIgnore
        @JsonAnyGetter
        @JsonAnySetter
        Map<String, Object> additionalProperties;

        String fingerprint;
        String region;
        String user;
        @JsonProperty("key_file")
        Path keyFile;
        String tenancy;
    }
}
