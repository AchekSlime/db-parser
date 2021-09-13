package stabbers.parserdb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "serializer")
public class SerializerConfig {
    /** path to .json file for serialization */
    private String pathJson;
    /** path to .png file for serialization */
    private String pathPng;
    /** path to .txt file for serialization */
    private String pathTxt;
    /** beginning of the uml file configuration template */
    private String umlInitConfig;
}

