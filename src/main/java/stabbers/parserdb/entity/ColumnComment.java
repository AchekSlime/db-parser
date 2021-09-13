package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class ColumnComment {
    private final String columnName;
    private final String comment;
}
