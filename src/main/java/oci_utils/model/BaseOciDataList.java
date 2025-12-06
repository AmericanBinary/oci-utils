package oci_utils.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseOciDataList<T extends BaseOciEntity> {
    List<T> data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("opc-next-page")
    String nextPage;
}
