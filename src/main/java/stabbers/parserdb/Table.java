package stabbers.parserdb;

import lombok.*;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Table {
    private String name;
    private List<Column> columns;
    //ToDo Добавить комментарии к таблице

    public Table(String name){
        this.name = name;
        columns = new LinkedList<>();
    }

    public void addColumn(Column column){
        columns.add(column);
    }

    @Override
    public String toString() {
        return "Table{\n" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                "}\n";
    }
}
