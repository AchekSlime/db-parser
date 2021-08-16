package stabbers.parserdb.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@JsonPropertyOrder({ "table_name", "table_comment", "columns", "foreignKeys" })
public class Table {
    private final String table_name;
    private String table_comment;
    private List<Column> columns;
    private List<ForeignKey> foreignKeys;
}
