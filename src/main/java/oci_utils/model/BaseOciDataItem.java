package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class BaseOciDataItem<T> {
    T data;
    @JsonIgnore
    @JsonAnyGetter
    @JsonAnySetter
    Map<String, Object> additionalProperties;
}
