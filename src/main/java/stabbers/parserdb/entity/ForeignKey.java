package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class ForeignKey {
    private final String tableName;
    private final String columnName;
    private final String foreignTableName;
    private final String foreignColumnName;
}
