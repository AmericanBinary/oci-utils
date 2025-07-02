package oci_utils.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseOciDataList<T> {
    List<T> data;
}
