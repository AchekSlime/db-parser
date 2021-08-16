package stabbers.parserdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class ParserDbApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ParserDbApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ParserDbApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        List<Table> tables = new LinkedList<>();

        jdbcTemplate.query("SELECT table_name\n" +
                "  FROM information_schema.tables\n" +
                " WHERE table_schema='public'\n" +
                "   AND table_type='BASE TABLE';", (rs, rn) -> new Table(rs.getString("table_name")))
                .forEach(table -> {
                    tables.add(table);
                    getColumns(table);
                });

        System.out.println(tables);

//        jdbcTemplate.query("\\d+ ?", new Object[]{"student"}, (rs, rn) -> new Table(rs.getString("Column"),
//                rs.getString("Type"), rs.getString("Description"))).forEach(table -> System.out.println(table.toString()));
    }

    private void getColumns(Table currentTable){
        jdbcTemplate.query("SELECT \n" +
                "   table_name, \n" +
                "   column_name, \n" +
                "   data_type\n" +
                "FROM \n" +
                "   information_schema.columns\n" +
                "WHERE \n" +
                "   table_name = ?;", new Object[] {currentTable.getName()}, (rs, rn) -> new Column(
                rs.getString("column_name"), rs.getString("data_type"))).forEach(currentTable::addColumn);
    }
}
