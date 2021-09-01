package stabbers.parserdb.serializer.uml;

import stabbers.parserdb.entity.Column;
import stabbers.parserdb.entity.Database;
import stabbers.parserdb.entity.Table;

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

    /**
     * Return database structure in the uml syntax as a string
     * @param db requested database
     * @return string - database structure in uml
     */
    public String getString(Database db){
        return umlInit + serializeTables(db) + foreignKeys + "@enduml";
    }

    /**
     * Returns all tables in the uml syntax as a string
     * @param db requested database
     * @return string - tables in uml syntax
     */
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

    /**
     * Returns all columns of requested table in the uml syntax as a string
     * @param table requested table
     * @return string - columns in uml syntax
     */
    private String serializeColumns(Table table) {
        StringBuilder stb = new StringBuilder();
        String indent = "  ";
        table.getColumns().forEach((column) -> {
            stb.append(indent).append(getColumnType(column)).append(" : ").append(column.getData_type());
            if(column.getColumn_comment() != null)
                stb.append(" \"").append(column.getColumn_comment()).append("\"");
            stb.append("\n");
        });
        return new String(stb.toString());
    }

    /**
     * Defines the format for the column depending on the constraints
     * @param column requested column
     * @return string - uml column name
     */
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

    /**
     * Adds the foreignKeys of the requested table to the global db relationships
     * @param table requested table
     */
    private void serializeTableFK(Table table){
        table.getForeignKeys().forEach(fk -> {
            foreignKeys.append(fk.getTable_name()).append("::").append(fk.getColumn_name());
            foreignKeys.append(" }o--|| ");
            foreignKeys.append(fk.getForeign_table_name()).append("::").append(fk.getForeign_column_name()).append(" : ").append(fk.getColumn_name());
            foreignKeys.append("\n");
        });
    }
}
