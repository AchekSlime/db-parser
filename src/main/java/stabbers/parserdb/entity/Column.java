package stabbers.parserdb.entity;

import lombok.Data;

@Data
public class Column {
    private final String column_name;
    private final String data_type;
    private String constraints;
    private String column_comment;

}
