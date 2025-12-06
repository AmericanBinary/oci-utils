package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Dto
public class SessionItem extends BaseOciEntity {
    String bastionId;
    String bastionName;
    @JsonProperty("bastion-public-host-key-info")
    JsonNode bastionPublicHostKeyInfo;
    @JsonProperty("bastion-user-name")
    String bastionUserName;
    @JsonProperty("key-details")
    KeyDetails keyDetails;
    @JsonProperty("key-type")
    KeyType keyType;
    @JsonProperty("lifecycle-details")
    JsonNode lifecycleDetails;
    @JsonProperty("lifecycle-state")
    LifecycleState lifecycleState;

    @JsonProperty("session-ttl-in-details")
    int sessionTtlInSeconds;
    @JsonProperty("ssh-metadata")
    SshMetadata sshMetadata;
    @JsonProperty("target-resource-details")
    TargetResourceDetails targetResourceDetails;
    @JsonProperty("time-created")
    Instant timeCreated;
    @JsonProperty("time-updated")
    Instant timeUpdated;

    public enum KeyType { PUB }

    public enum LifecycleState { CREATING, ACTIVE, DELETED }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KeyDetails {
        @JsonProperty("public-key-content")
        String publicKeyContent;
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SshMetadata {
        String command;
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TargetResourceDetails {
        @JsonProperty("session-type")
        SessionType sessionType;
        @JsonProperty("target-resource-display-name")
        String targetResourceDisplayName;
        @JsonProperty("target-resource-fqdn")
        String targetResourceFqdn;
        @JsonProperty("target-resource-id")
        String targetResourceId;
        @JsonProperty("target-resource-port")
        int targetResourcePort;
        @JsonProperty("target-resource-private-ip-address")
        String targetResourcePrivateIpAddress;

        public enum SessionType {
            PORT_FORWARDING
        }
    }
}
