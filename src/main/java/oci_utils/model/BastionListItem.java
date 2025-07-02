package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BastionListItem extends BaseOciEntity {
    @JsonProperty("bastion-type")
    String bastionType;
    @JsonProperty("compartment-id")
    String compartmentId;
    @JsonProperty("dns-proxy-status")
    String dnsProxyStatus;
    @JsonProperty("lifecycle-details")
    JsonNode lifecycleDetails;
    @JsonProperty("lifecycle-status")
    String lifecycleStatus;
    @JsonProperty("system-tags")
    Map<String, Object> systemTags;
    @JsonProperty("target-subnet-id")
    String targetSubnetId;
    @JsonProperty("target-vcn-id")
    String targetVcnId;
    @JsonProperty("time-created")
    Instant timeCreated;
    @JsonProperty("time-updated")
    Instant timeUpdated;
}
