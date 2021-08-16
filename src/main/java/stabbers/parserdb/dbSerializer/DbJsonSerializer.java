package stabbers.parserdb.dbSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.entity.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class DbJsonSerializer {
    private final JdbcTemplate jdbcTemplate;
    private final Database db;

    public DbJsonSerializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        db = new Database("test", getTables());
    }

    private LinkedList<Table> getTables() {
        LinkedList<Table> tables = new LinkedList<>(jdbcTemplate.query(
                "SELECT table_name\n" +
                        "FROM information_schema.tables\n" +
                        "WHERE table_schema='public' AND table_type='BASE TABLE';",
                (rs, rn) -> new Table(rs.getString("table_name"))
        ));

        tables.forEach(table -> {
            HashMap<String, String> columnComments = getColumnComments(table.getTable_name());
            table.setTable_comment(getTableComment(table.getTable_name()));
            table.setColumns(getColumns(table.getTable_name()));
            table.getColumns().forEach(column -> column.setColumn_comment(columnComments.get(column.getColumn_name())));
            table.setForeignKeys(getForeignKeys(table.getTable_name()));
        });

        return tables;
    }

    private String getTableComment(String table_name) {
        try {
            return jdbcTemplate.queryForObject("SELECT obj_description(oid) \"comment\"\n" +
                            "FROM pg_class\n" +
                            "WHERE relkind = 'r' AND relname = ?;",
                    (rs, rn) -> rs.getString("comment"),
                    table_name
            );
        } catch (DataAccessException ex) {
            return null;
        }
    }

    private LinkedList<Column> getColumns(String table_name) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT column_name, data_type\n" +
                        "FROM information_schema.columns\n" +
                        "WHERE table_name = ?;",
                (rs, rn) -> new Column(rs.getString("column_name"), rs.getString("data_type")),
                table_name)
        );
    }

    private HashMap<String, String> getColumnComments(String table_name) {
        HashMap<String, String> comments = new HashMap<>();
        LinkedList<ColumnComment> commentsList = new LinkedList<>(jdbcTemplate.query(
                "SELECT c.column_name, pgd.description\n" +
                        "FROM pg_catalog.pg_statio_all_tables as st\n" +
                        "  inner join pg_catalog.pg_description pgd on (pgd.objoid=st.relid)\n" +
                        "  inner join information_schema.columns c on (pgd.objsubid=c.ordinal_position\n" +
                        "    and  c.table_schema=st.schemaname and c.table_name=st.relname)\n" +
                        "WHERE c.table_name = ?;",
                (rs, rn) -> new ColumnComment(rs.getString("column_name"), rs.getString("description")),
                table_name
        ));
        commentsList.forEach(comment -> comments.put(comment.getColumn_name(), comment.getComment()));
        return comments;
    }

    private LinkedList<ForeignKey> getForeignKeys(String table_name) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT \n" +
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

    public void serialize(String path) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try (BufferedWriter file = new BufferedWriter(new FileWriter(path))) {
            mapper.writeValue(file, db);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
