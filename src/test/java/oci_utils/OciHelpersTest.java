package oci_utils;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import oci_utils.model.BaseOciDataList;
import oci_utils.model.CompartmentListItem;
import oci_utils.model.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class OciHelpersTest {

    OciHelpers ociHelpers;
    JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        ociHelpers = spy(new OciHelpers());
        doReturn(new Config.Profile().setTenancy("tenancy")).when(ociHelpers).getOrLoadDefaultProfile();

        jsonMapper = JsonMapper.builder().findAndAddModules().build();
    }

    void mockRunMethod(String command, String output) {
        doReturn(new OciHelpers.RunResult(output, "", 0))
                .when(ociHelpers)
                .run(command);

    }

    @SneakyThrows
    @Test
    void test_getCompartment() {
        mockRunMethod("oci iam compartment list --compartment-id tenancy --name test-compartment",
                jsonMapper.writeValueAsString(new BaseOciDataList<CompartmentListItem>()
                        .setData(List.of((CompartmentListItem) new CompartmentListItem().setId("1").setName("name")))));
        CompartmentListItem compartment = ociHelpers.getCompartment("test-compartment");
        assertEquals("1", compartment.getId());
        assertEquals("name", compartment.getName());
    }

    @SneakyThrows
    @Test
    void test_getCompartment_multipleException() {
        mockRunMethod("oci iam compartment list --compartment-id tenancy --name test-multiple",
                jsonMapper.writeValueAsString(new BaseOciDataList<CompartmentListItem>()
                        .setData(List.of(
                                (CompartmentListItem) new CompartmentListItem().setId("1").setName("name"),
                                (CompartmentListItem) new CompartmentListItem().setId("2").setName("name2")
                        ))));
        var mre = assertThrows(OciHelpers.MultipleResultError.class, () -> ociHelpers.getCompartment("test-multiple"));
        assertEquals(List.of("name", "name2"), mre.getNames());
        assertEquals("Expected one item but found these instead: " + String.join(", ", List.of("name", "name2")), mre.getMessage());
    }
}
