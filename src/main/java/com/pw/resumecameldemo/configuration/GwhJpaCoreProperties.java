package com.pw.resumecameldemo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "gwh.core.db.datasource")
public class GwhJpaCoreProperties {

    String className;
    
    String url;

    String userName;
   
    Integer minPoolSize = 0;

    Integer maxPoolSize = 20;

    Integer increment = 2;

    Integer maxIdleTime = 300;

    Integer retryDelay = 600000;

    String jpaDdlAuto = "validate";

    String jpaDialect = "org.hibernate.dialect.MySQLDialect";

    String jpaPhysicalNamingStrategy = "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy";
    
}
