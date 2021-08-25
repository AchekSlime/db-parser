package stabbers.parserdb.serializer;

import stabbers.parserdb.entity.Database;
import stabbers.parserdb.entity.ForeignKey;
import stabbers.parserdb.entity.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class PlantumlSerializer {
    private static final HashMap<String, LinkedList<String>> fk = new HashMap<>();

    public static void serialize(Database db, String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write("@startuml\nhide circle\nskinparam linetype ortho\n\n");
            bw.write(serializeTables(db));
            bw.write(serializeGlobalFk());
            bw.write("@enduml");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        fk.clear();
    }

    private static String serializeTables(Database db){
        StringBuilder ans = new StringBuilder();
        db.getTables().forEach(table -> {
            fk.put(table.getTable_name(), new LinkedList<>());
            ans.append("entity \"").append(table.getTable_name()).append("\" as ").append(table.getTable_name()).append(" {\n");
            ans.append(serializeColumns(table));
            ans.append("  --\n");
            ans.append("  table comment : ").append(table.getTable_comment()).append("\n");
            ans.append(serializeTableFK(table));
            ans.append("}\n\n");
        });
        return ans.toString();
    }

    private static String serializeColumns(Table table) {
        StringBuilder ans = new StringBuilder();
        String indent = "  ";
        table.getColumns().forEach((column) -> {
            ans.append(indent).append("*").append(column.getColumn_name()).append(" : ").append(column.getData_type());
            if(column.getColumn_comment() != null)
                ans.append(" \"").append(column.getColumn_comment()).append("\"");
            ans.append("\n");
        });
        return ans.toString();
    }

    private static String serializeTableFK(Table table){
        StringBuilder ans = new StringBuilder();
        String indent = "  ";
        table.getForeignKeys().forEach(key -> {
            ans.append(indent).append("fk : ").append(table.getTable_name()).append(".").append(key.getColumn_name()).append(" -> ")
                    .append(key.getForeign_table_name()).append(".").append(key.getForeign_column_name()).append("\n");
            fk.get(table.getTable_name()).add(key.getForeign_table_name());
        });
        return ans.toString();
    }

    //Todo Обработать остальные виды связей
    private static String serializeGlobalFk(){
        StringBuilder ans = new StringBuilder();
        fk.forEach((k, v) -> v.forEach(fTable -> ans.append(k).append(" }o--|| ").append(fTable).append("\n")));
        return ans.toString();
    }
}
