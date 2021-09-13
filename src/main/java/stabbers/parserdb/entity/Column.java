package stabbers.parserdb.entity;

import lombok.Data;

import java.util.List;

@Data
public class Column {
    private final String columnName;
    private final String dataType;
    private List<String> constraints;
    private String columnComment;

}
