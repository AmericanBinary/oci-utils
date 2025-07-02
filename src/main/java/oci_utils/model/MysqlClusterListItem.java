package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MysqlClusterListItem extends BaseOciEntity {
    List<Endpoint> endpoints;

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Endpoint {
        String hostname;
        @JsonProperty("ip-address")
        String ipAddress;
        Set<Mode> modes;
        int port;
        @JsonProperty("port-x")
        int portX;
        @JsonProperty("resource-id")
        String resourceId;
        String status;
        JsonNode statusDetails;

        public enum Mode {
            READ, WRITE
        }
    }
}
