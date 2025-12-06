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
public class ComputeImageItem extends BaseOciEntity {
    @JsonProperty("base-image-id")
    String baseImageId;
    @JsonProperty("billable-size-in-gbs")
    int billableSizeInGbs;
    @JsonProperty("compartment-id")
    String compartmentId;
    @JsonProperty("create-image-allowed")
    Boolean createImageAllowed;
    @JsonProperty("launch-mode")
    String launchMode;
    @JsonProperty("launch-options")
    LaunchOptions launchOptions;
    @JsonProperty("lifecycle-state")
    String lifecycleState;
    @JsonProperty("listing-type")
    String listingType;
    @JsonProperty("operating-system")
    String operatingSystem;
    @JsonProperty("operating-system-version")
    String operatingSystemVersion;
    @JsonProperty("size-in-mbs")
    int sizeInMbs;
    @JsonProperty("time-created")
    String timeCreated;

    @Data
    @Accessors(chain = true)
    @Dto
    public static class LaunchOptions {
        @JsonProperty("boot-volume-type")
        String bootVolumeType;
        String firmware;
        @JsonProperty("is-consistent-volume-naming-enabled")
        Boolean isConsistentVolumeNamingEnabled;
        @JsonProperty("is-pv-encryption-in-transit-enabled")
        Boolean isPvEncryptionInTransitEnabled;
        @JsonProperty("network-type")
        String networkType;
        @JsonProperty("remote-data-volume-type")
        String remoteDataVolumeType;
    }
}
