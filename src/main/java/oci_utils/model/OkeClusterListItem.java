package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Dto
public class OkeClusterListItem extends BaseOciEntity {
    Endpoints endpoints;

    Map<String, String> metadata;
    Map<String, Object> options;

    @Data
    @Accessors(chain = true)
    @Dto
    public static class Endpoints {
        @JsonProperty("ipv6-endpoint")
        String ipv6Endpoint;
        @JsonProperty("kubernetes")
        String kubernetes;
        @JsonProperty("private-endpoint")
        String privateEndpoint;
        @JsonProperty("public-endpoint")
        String publicEndpoint;
        @JsonProperty("vcn-hostname-endpoint")
        String vcnHostnameEndpoint;
    }
}
