package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class Constraint {
    private final String column_name;
    private final String constraint;
}
