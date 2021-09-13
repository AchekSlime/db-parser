package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class Constraint {
    private final String columnName;
    private final String constraint;
}
