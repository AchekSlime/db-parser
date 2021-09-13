package stabbers.parserdb.serializer.uml;

import stabbers.parserdb.entity.Column;
import stabbers.parserdb.entity.Database;
import stabbers.parserdb.entity.Table;

public class UmlConverter {
    private final StringBuilder foreignKeys;
    private final String umlInit;

    public UmlConverter(String umlInitConfig){
        this.umlInit = umlInitConfig;
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
            if(!table.getTableName().equals("databasechangelog") && !table.getTableName().equals("databasechangeloglock")){
                stb.append("table( ").append(table.getTableName()).append(" ) {").append(System.lineSeparator());
                stb.append(serializeColumns(table));
                stb.append("  --").append(System.lineSeparator());
                stb.append("heap_column( table comment ) : ").append(table.getTableComment()).append(System.lineSeparator());
                serializeTableFK(table);
                stb.append("}").append(System.lineSeparator()).append(System.lineSeparator());
            }
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
            stb.append(indent).append(getColumnType(column)).append(" : ").append(column.getDataType());
            if(column.getColumnComment() != null)
                stb.append(" \"").append(column.getColumnComment()).append("\"");
            stb.append(System.lineSeparator());
        });
        return stb.toString();
    }

    /**
     * Defines the format for the column depending on the constraints
     * @param column requested column
     * @return string - uml column name
     */
    private String getColumnType(Column column){
        if(column.getConstraints() == null)
            return "column( " + column.getColumnName() + " )";
        else {
            if (column.getConstraints().contains("u")){
                // ToDo отрисовать.
            }
            if (column.getConstraints().contains("p"))
                return "primary_key( " + column.getColumnName() + " )";
            else if(column.getConstraints().contains("f"))
                return "foreign_key( " + column.getColumnName() + " )";
            else
                return "*" + column.getColumnName();
        }

    }

    /**
     * Adds the foreignKeys of the requested table to the global db relationships
     * @param table requested table
     */
    private void serializeTableFK(Table table){
        table.getForeignKeys().forEach(fk -> {
            foreignKeys.append(fk.getTableName()).append("::").append(fk.getColumnName());
            foreignKeys.append(" }o--|| ");
            foreignKeys.append(fk.getForeignTableName()).append("::").append(fk.getForeignColumnName()).append(" : ").append(fk.getColumnName());
            foreignKeys.append(System.lineSeparator());
        });
    }
}
