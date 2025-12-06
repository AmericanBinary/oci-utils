package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Dto
public class TenancyItem extends BaseOciEntity {
    @JsonProperty("home-region-key")
    String homeRegionKey;
    @JsonProperty("upi-idcs-compatibility-layer-endpoint")
    String idcsCompatibilityLayerEndpoint;
}
