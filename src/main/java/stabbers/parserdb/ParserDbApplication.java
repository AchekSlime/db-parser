package stabbers.parserdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.service.DbService;
import stabbers.parserdb.serializer.DbJsonSerializer;

@SpringBootApplication
public class ParserDbApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ParserDbApplication.class);
    private final JdbcTemplate jdbcTemplate;

    public ParserDbApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(ParserDbApplication.class, args);
    }

    @Override
    public void run(String... args) {
        DbService serializer = new DbService(jdbcTemplate);
        DbJsonSerializer.serialize(serializer.getDb());
        log.info("Sterilization is completed");
    }


}
