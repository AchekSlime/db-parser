package stabbers.parserdb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Database {
    private String db_name;
    private List<Table> tables;
}
