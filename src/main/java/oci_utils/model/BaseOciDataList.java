package oci_utils.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseOciDataList<T extends BaseOciEntity> {
    List<T> data;
}
