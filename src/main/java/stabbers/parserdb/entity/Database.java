package stabbers.parserdb.entity;

import lombok.Data;

import java.util.List;

@Data
public class Database {
    private final String db_name;
    private final List<Table> tables;
}
