package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class ForeignKey {
    private final String table_name;
    private final String column_name;
    private final String foreign_table_name;
    private final String foreign_column_name;
}
