package stabbers.parserdb.dbSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.entity.Column;
import stabbers.parserdb.entity.Database;
import stabbers.parserdb.entity.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class DbJsonSerializer {
    private final JdbcTemplate jdbcTemplate;
    private final Database db;

    public DbJsonSerializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        db = new Database("test", getTables());
    }

    private LinkedList<Table> getTables() {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT table_name\n" +
                        "FROM information_schema.tables\n" +
                        "WHERE table_schema='public' AND table_type='BASE TABLE';",
                (rs, rn) -> new Table(rs.getString("table_name"), getTableColumns(rs.getString("table_name")))
        ));
    }

    private LinkedList<Column> getTableColumns(String table_name) {
        return new LinkedList<>(jdbcTemplate.query(
                "SELECT column_name, data_type\n" +
                        "FROM information_schema.columns\n" +
                        "WHERE table_name = ?;",
                (rs, rn) -> new Column(rs.getString("column_name"), rs.getString("data_type")),
                table_name)
        );
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
