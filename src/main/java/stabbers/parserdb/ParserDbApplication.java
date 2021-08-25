package stabbers.parserdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.config.DataSourceConfig;
import stabbers.parserdb.config.SerializerConfig;
import stabbers.parserdb.serializer.PlantumlSerializer;
import stabbers.parserdb.service.DbService;
import stabbers.parserdb.serializer.JsonSerializer;

@SpringBootApplication
public class ParserDbApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ParserDbApplication.class);
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private SerializerConfig sConfig;
    @Autowired
    private DataSourceConfig dConfig;

    public ParserDbApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public static void main(String[] args) {
        SpringApplication.run(ParserDbApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Создаем инстанс класса, который вытянет структуру бд и замапит ее в сущности.
        DbService dbService = new DbService(jdbcTemplate, dConfig.getDb_name());
        // Подтягиваем всю структуру бд.
        dbService.configure();

        // Сериализуем полученную структуру из сущностей в JSON.
        JsonSerializer.serialize(dbService.getDb(), sConfig.getPath() + ".json");
        // Сериализуем в PlantUML
        PlantumlSerializer.serialize(dbService.getDb(), sConfig.getPath() + "[plant].txt");

        log.info("...Serialization is completed...");
    }


}
