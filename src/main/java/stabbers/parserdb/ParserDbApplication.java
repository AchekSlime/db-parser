package stabbers.parserdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.config.DataSourceConfig;
import stabbers.parserdb.config.SerializerConfig;
import stabbers.parserdb.serializer.uml.UmlSerializer;
import stabbers.parserdb.service.DbService;
import stabbers.parserdb.serializer.json.JsonSerializer;

@SpringBootApplication
public class ParserDbApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ParserDbApplication.class);
    private final JdbcTemplate jdbcTemplate;
    private final SerializerConfig serializerConfig;
    private final DataSourceConfig dataSourceConfig;

    public ParserDbApplication(JdbcTemplate jdbcTemplate, SerializerConfig serializerConfig, DataSourceConfig dataSourceConfig) {
        this.jdbcTemplate = jdbcTemplate;
        this.serializerConfig = serializerConfig;
        this.dataSourceConfig = dataSourceConfig;
    }


    public static void main(String[] args) {
        SpringApplication.run(ParserDbApplication.class, args);
    }

    @Override
    public void run(String... args) {
        /*
            Маппинг базы данных в сущность
         */
        // Создаем инстанс класса, который вытянет структуру бд и замапит ее в сущности.
        DbService dbService = new DbService(jdbcTemplate, dataSourceConfig.getDb_name(), dataSourceConfig.getSchema_name());
        // Подтягиваем всю структуру бд.
        dbService.configure();


        /*
            Сериализация
         */
        // Сериализуем полученную структуру в JSON.
        JsonSerializer.serialize(serializerConfig.getPathJson(), dbService.getDb());
        // Сериализация в png диаграму
        UmlSerializer.serializeToPng( serializerConfig.getPathPng(), dbService.getDb());
        // Сериадизация в uml
        UmlSerializer.serializeToTxt(serializerConfig.getPathTxt(), dbService.getDb());

        log.info("...Serialization is completed...");
    }
}
