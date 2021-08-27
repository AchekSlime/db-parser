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
    private String pathJson;
    private String pathPng;
    private String pathTxt;
}

