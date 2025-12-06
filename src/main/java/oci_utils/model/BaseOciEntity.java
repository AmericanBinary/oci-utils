package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
@Dto
public abstract class BaseOciEntity {
    String id;
    @JsonAlias("display-name")
    String name;
    String description;
    @JsonProperty("defined-tags")
    Map<String, Map<String, String>> definedTags;
    @JsonProperty("freeform-tags")
    Map<String, String> freeFormTags;
}
