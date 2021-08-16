package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class ColumnComment {
    private final String column_name;
    private final String comment;
}
