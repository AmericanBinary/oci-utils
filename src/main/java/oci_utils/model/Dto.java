package oci_utils.model;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@JacksonAnnotationsInside
@JsonIgnoreProperties(ignoreUnknown = true)
public @interface Dto {
}
