package stabbers.parserdb.serializer.uml;

import stabbers.parserdb.entity.Column;
import stabbers.parserdb.entity.Database;
import stabbers.parserdb.entity.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UmlConverter {
    private final StringBuilder foreignKeys;
    private static final String umlInit = "@startuml\nleft to right direction\n" +
                "!define foreign_key(x) <color:#aaaaaa><&key></color> x\n" +
                "!define primary_key(x) <b><color:#b8861b><&key></color> x<b>\n" +
                "!define column(x) <color:#000000><&media-record></color> x\n" +
                "!define heap_column(x) <color:#808080><&media-record></color> x\n" +
                "!define table(x) entity x << (T, white) >> " +
                "\n\n";

    public UmlConverter(){
        foreignKeys = new StringBuilder();
    }

    public String getString(Database db){
        return umlInit + serializeTables(db) + foreignKeys + "@enduml";
    }

    private String serializeTables(Database db){
        StringBuilder stb = new StringBuilder();
        db.getTables().forEach(table -> {
            stb.append("table( ").append(table.getTable_name()).append(" ) { \n");
            stb.append(serializeColumns(table));
            stb.append("  --\n");
            stb.append("heap_column( table comment ) : ").append(table.getTable_comment()).append("\n");
            serializeTableFK(table);
            stb.append("}\n\n");
        });
        return stb.toString();
    }

    private String serializeColumns(Table table) {
        StringBuilder stb = new StringBuilder();
        String indent = "  ";
        table.getColumns().forEach((column) -> {
            stb.append(indent).append(getColumnType(column)).append(" : ").append(column.getData_type());
            if(column.getColumn_comment() != null)
                stb.append(" \"").append(column.getColumn_comment()).append("\"");
            stb.append("\n");
        });
        return stb.toString();
    }

    private String getColumnType(Column column){
        if(column.getConstraints() == null)
            return "column( " + column.getColumn_name() + " )";
        else if(column.getConstraints().contains("p"))
            return "primary_key( " + column.getColumn_name() + " )";
        else if(column.getConstraints().contains("f"))
            return "foreign_key( " + column.getColumn_name() + " )";
        else
            return "*" + column.getColumn_name();
    }

    private void serializeTableFK(Table table){
        table.getForeignKeys().forEach(fk -> {
            foreignKeys.append(fk.getTable_name()).append("::").append(fk.getColumn_name());
            foreignKeys.append(" }o--|| ");
            foreignKeys.append(fk.getForeign_table_name()).append("::").append(fk.getForeign_column_name()).append(" : ").append(fk.getColumn_name());
            foreignKeys.append("\n");
        });
    }

    public void serialize(Database db, String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write("@startuml\n!theme plain\nleft to right direction\n" +
                "!define primary_key(x) <b><color:#b8861b><&key></color> x<b>\n" +
                "!define foreign_key(x) <color:#aaaaaa><&key></color> x\n" +
                "!define column(x) <color:#000000><&media-record></color> x\n" +
                "!define table(x) entity x << (T, white) >> " +
                    "\n\n");
            bw.write(serializeTables(db));
            bw.write(foreignKeys.toString());
            bw.write("@enduml");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
