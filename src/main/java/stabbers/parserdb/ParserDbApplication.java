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
        // Создаем инстанц класса, который вытянет структуру бд и замапит ее в сущности.
        // После того, как вызовется конструктор, вся структура будет получена.
        DbService serializer = new DbService(jdbcTemplate);

        // Сериализуем полученную структуру из сущностей в JSON.
        DbJsonSerializer.serialize(serializer.getDb());
        log.info("Sterilization is completed");
    }


}
