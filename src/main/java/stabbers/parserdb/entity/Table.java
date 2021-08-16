package stabbers.parserdb.entity;

import lombok.Data;

import java.util.List;

@Data
public class Table {
    private final String table_name;
    private final List<Column> columns;
    //ToDo Добавить комментарии к таблице
}
