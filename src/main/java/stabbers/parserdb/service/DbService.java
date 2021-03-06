package stabbers.parserdb.service;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.entity.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.AbstractMap;

public class DbService {
    private final JdbcTemplate jdbcTemplate;
    private final Database db;
    private final String schema;

    /**
     * Constructs a DbService with database "db" and jdbcTemplate initialization
     *
     * @param jdbcTemplate the jdbcTemplate instance through which queries to the database will be executed
     * @throws org.springframework.dao.DataAccessException while sql query execution in getTables()
     */
    public DbService(JdbcTemplate jdbcTemplate, String dbName, String schema) {
        this.jdbcTemplate = jdbcTemplate;
        db = new Database(dbName, null);
        this.schema = schema;
    }

    /**
     * Retrieves the entire database structure
     */
    public void configure() {
        db.setTables(getTables());
    }

    /**
     * @return configured database instance with all entire structure
     */
    public Database getDb() {
        return db;
    }

    /**
     * Gets all tables with their internal structure
     *
     * @return List of configured Tables with their entire structure
     */
    private List<Table> getTables() {
        List<Table> tables = new LinkedList<>(jdbcTemplate.query(
                "SELECT table_name\n" +
                        "FROM information_schema.tables\n" +
                        "WHERE table_schema= ?;",
                (rs, rn) -> new Table(rs.getString("table_name")),
                schema
        ));

        tables.forEach(table -> {
            // Getting all the commented columns in current Table.
            HashMap<String, String> columnComments = getColumnComments(table.getTableName());
            // Setting the table comment.
            table.setTableComment(getTableComment(table.getTableName()));
            table.setColumns(getColumns(table.getTableName()));
            // Setting comments for each column in current table.
            table.getColumns().forEach(column -> column.setColumnComment(columnComments.get(column.getColumnName())));
            // Setting fKeys for each column.
            List<ForeignKey> fk = getForeignKeys(table.getTableName());
            // Setting fKeys
            table.setForeignKeys(fk);

            fk.forEach(fKey ->
                    table.getColumns().stream()
                            .filter(column -> fKey.getColumnName().equals(column.getColumnName())).findFirst()
                            .ifPresent(targetColumn ->  {
                                if(targetColumn.getConstraints() == null)
                                    targetColumn.setConstraints(new LinkedList<>());
                                targetColumn.getConstraints().add("f");
                            }));
        });

        return tables;
    }

    /**
     * Gets comment of table
     *
     * @param table_name name of the requested table
     * @return comment of the requested table
     */
    private String getTableComment(String table_name) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT obj_description(oid) \"comment\"\n" +
                            "FROM pg_class\n" +
                            "WHERE relkind = 'r' AND relname = ?;",
                    (rs, rn) -> rs.getString("comment"),
                    table_name
            );
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Gets all columns for requested table
     *
     * @param table_name name of the requested table
     * @return column List for requested table
     */
    private List<Column> getColumns(String table_name) {
        List<Column> columns = new LinkedList<>(jdbcTemplate.query(
                "SELECT column_name, data_type\n" +
                        "FROM information_schema.columns\n" +
                        "WHERE table_name = ?;",
                (rs, rn) -> new Column(rs.getString("column_name"), rs.getString("data_type")),
                table_name)
        );
        List<Constraint> constraints = getConstraints(table_name);
        constraints.forEach(constraint ->
                columns.stream()
                        .filter(column -> constraint.getColumnName().equals(column.getColumnName())).findFirst()
                        .ifPresent(targetColumn -> {
                                if(targetColumn.getConstraints() == null)
                                    targetColumn.setConstraints(new LinkedList<>());
                                targetColumn.getConstraints().add(constraint.getConstraint());
                        }
                        )
        );
        return columns;
    }

    /**
     * Gets all column_comments in the form of Map
     *
     * @param table_name name of the requested table
     * @return Map<column_name, column_comment></> for requested table
     */
    private HashMap<String, String> getColumnComments(String table_name) {
        HashMap<String, String> comments = new HashMap<>();
        List<AbstractMap.SimpleEntry<String, String>> commentsList = new LinkedList <>(jdbcTemplate.query(
                "SELECT c.column_name, pgd.description\n" +
                        "FROM pg_catalog.pg_statio_all_tables as st\n" +
                        "  inner join pg_catalog.pg_description pgd on (pgd.objoid=st.relid)\n" +
                        "  inner join information_schema.columns c on (pgd.objsubid=c.ordinal_position\n" +
                        "    and  c.table_schema=st.schemaname and c.table_name=st.relname)\n" +
                        "WHERE c.table_name = ?;",
                (rs, rn) -> new AbstractMap.SimpleEntry<>(rs.getString("column_name"), rs.getString("description")),
                table_name
        ));
        commentsList.forEach(comment -> comments.put(comment.getKey(), comment.getValue()));
        return comments;
    }

    /**
     * Gets all foreign keys constraints for requested table
     *
     * @param table_name ame of the requested table
     * @return ForeignKey list with their structure
     */
    private List<ForeignKey> getForeignKeys(String table_name) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT\n" +
                        "    tc.table_name, \n" +
                        "    kcu.column_name, \n" +
                        "    ccu.table_name AS foreign_table_name,\n" +
                        "    ccu.column_name AS foreign_column_name \n" +
                        "FROM \n" +
                        "    information_schema.table_constraints AS tc \n" +
                        "    JOIN information_schema.key_column_usage AS kcu\n" +
                        "      ON tc.constraint_name = kcu.constraint_name\n" +
                        "      AND tc.table_schema = kcu.table_schema\n" +
                        "    JOIN information_schema.constraint_column_usage AS ccu\n" +
                        "      ON ccu.constraint_name = tc.constraint_name\n" +
                        "      AND ccu.table_schema = tc.table_schema\n" +
                        "WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_name= ?;",
                (rs, rn) -> new ForeignKey(rs.getString("table_name"), rs.getString("column_name"),
                        rs.getString("foreign_table_name"), rs.getString("foreign_column_name")),
                table_name
        ));
    }

    private List<Constraint> getConstraints(String table_name) {
        LinkedList<Constraint> constraints = new LinkedList<>();
        jdbcTemplate.query(
                "select\n" +
                        "ccu.column_name,\n" +
                        "contype\n" +
                        "from \n" +
                        "pg_constraint pgc\n" +
                        "join pg_namespace nsp on nsp.oid = pgc.connamespace\n" +
                        "join pg_class  cls on pgc.conrelid = cls.oid\n" +
                        "left join information_schema.constraint_column_usage ccu\n" +
                        "          on pgc.conname = ccu.constraint_name\n" +
                        "          and nsp.nspname = ccu.constraint_schema\n" +
                        "where contype!='f' and ccu.table_name = ?" +
                        "order by pgc.conname;",
                (rs, rn) -> constraints.add(new Constraint(rs.getString("column_name"), rs.getString("contype"))),
                table_name
        );
        return constraints;
    }

//    private List<Pair<>> getConstraints2(String table_name) {
//        LinkedList<Constraint> constraints = new LinkedList<>();
//        jdbcTemplate.query(
//                "select\n" +
//                        "ccu.column_name,\n" +
//                        "contype\n" +
//                        "from \n" +
//                        "pg_constraint pgc\n" +
//                        "join pg_namespace nsp on nsp.oid = pgc.connamespace\n" +
//                        "join pg_class  cls on pgc.conrelid = cls.oid\n" +
//                        "left join information_schema.constraint_column_usage ccu\n" +
//                        "          on pgc.conname = ccu.constraint_name\n" +
//                        "          and nsp.nspname = ccu.constraint_schema\n" +
//                        "where contype!='f' and ccu.table_name = ?" +
//                        "order by pgc.conname;",
//                (rs, rn) -> constraints.add()),
//                table_name
//        );
//        return constraints;
//    }

}
