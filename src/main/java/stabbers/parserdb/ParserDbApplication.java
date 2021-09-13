package stabbers.parserdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import stabbers.parserdb.config.DataSourceConfig;
import stabbers.parserdb.config.SerializerConfig;
import stabbers.parserdb.confluence.Confluence;
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
        serialization();
        api();
    }

    private void serialization(){
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
        UmlSerializer serializer = new UmlSerializer();
        serializer.serialize(serializerConfig.getPathTxt(), serializerConfig.getUmlInitConfig(), dbService.getDb());

        log.info("...Serialization is completed...");
    }

    private void api(){
        String url = "https://impsface.atlassian.net/wiki/rest/api/content/196616";
        String token = "Basic aW1wc2ZhY2VAeWFuZGV4LnJ1OkhXQk9sZXllWVlNV09VUHRQa1h4Qzg1Ng==";
        String path = serializerConfig.getPathPng();

        Confluence api = new Confluence(url, token, path);
        api.addImage();
        api.update();
    }
}
