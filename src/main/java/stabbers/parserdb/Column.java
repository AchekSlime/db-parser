package stabbers.parserdb;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Column {
    //private String table_name;
    private String column_name;
    private String data_type;
    //ToDo Добавить комментарий
    //ToDo Доабавить внешние констрейнты
}
