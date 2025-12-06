package oci_utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vandermeer.asciitable.AsciiTable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import oci_utils.model.BaseOciDataItem;
import oci_utils.model.BaseOciDataList;
import oci_utils.model.BaseOciEntity;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class DataStringFormatter {
    final ObjectMapper objectMapper;

    @SneakyThrows
    public String formatData(Object o, Format format, List<String> tableColumns) {
        return switch (format) {
            case JSON -> objectMapper.writeValueAsString(o);
            case JSON_PRETTY -> objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(o);
            case TABLE -> table(o, tableColumns);
        };
    }

    private String table(Object o, List<String> tableColumns) {
        AsciiTable asciiTable = new AsciiTable();
        asciiTable.addRule();

        switch (o) {
            case BaseOciEntity entity -> addList(asciiTable, List.of(entity), tableColumns);
            case BaseOciDataItem<?> item -> addList(asciiTable, List.of(item.getData()), tableColumns);
            case BaseOciDataList<?> list -> addList(asciiTable, list.getData(), tableColumns);
            case Collection<?> list -> addUnrecognized(asciiTable, list, tableColumns);
            case null, default -> addUnrecognized(asciiTable, Collections.singletonList(o), tableColumns);
        }

        asciiTable.addRule();
        asciiTable.setPaddingLeft(1);
        asciiTable.setPaddingRight(1);
        int width = getWidth();
        return asciiTable.render(width);
    }

    private void addUnrecognized(AsciiTable asciiTable, Collection<?> l, List<String> tableColumns) {
        if (!l.isEmpty() && l.stream().findAny().orElse(null) instanceof BaseOciEntity) {
            addList(asciiTable, l.stream().filter(BaseOciEntity.class::isInstance).map(BaseOciEntity.class::cast).toList(), tableColumns);
            return;
        }

        asciiTable.addRow("data");
        asciiTable.addRule();
        for (Object o : l) {
            asciiTable.addRow(formatData(o, Format.JSON_PRETTY, tableColumns));
        }
    }

    private void addList(AsciiTable asciiTable,
                         List<? extends BaseOciEntity> data,
                         List<String> tableColumns) {
        asciiTable.addRow(tableColumns);
        asciiTable.addRule();
        for (BaseOciEntity entity : data) {
            var tree = objectMapper.valueToTree(entity);
            asciiTable.addRow(tableColumns.stream()
                    .map(c -> tree.path(c).asText())
                    .toList());
        }
    }

    private int getWidth() {
        try (Terminal terminal = TerminalBuilder.terminal()) {
            int detectedWidth = terminal.getWidth();
            if (detectedWidth < 40) {
                return 80;
            }
            return detectedWidth;
        } catch (Exception e) {
            return 80;
        }
    }

    public enum Format {
        JSON,
        JSON_PRETTY,
        // CSV, // future
        TABLE,
    }
}
