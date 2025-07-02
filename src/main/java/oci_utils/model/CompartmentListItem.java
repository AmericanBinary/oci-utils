package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CompartmentListItem extends BaseOciEntity {
    @JsonProperty("compartment-id")
    String compartmentId;
    @JsonProperty("inactive-status")
    String inactiveStatus;
    @JsonProperty("is-accessible")
    Boolean isAccessible;
    @JsonProperty("lifecycle-state")
    String lifecycleState;
    @JsonProperty("time-created")
    Instant timeCreated;
}
