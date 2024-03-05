package com.pw.resumecameldemo.configuration;

import java.beans.PropertyVetoException;
import java.io.File;
import javax.sql.DataSource;
import org.apache.camel.component.caffeine.resume.CaffeineCache;
import org.apache.camel.resume.cache.ResumeCache;
import org.apache.camel.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pw.resumecameldemo.resume.TableResumeStrategyConfigurationBuilder;

@Configuration
public class MyConfig {

    @Autowired
    GwhJpaCoreProperties gwhJpaCoreProperties;

    @Value("${input.dir}")
    private String consumerDirectory;

    @Value("${input.file}")
    private String consumerFile;

    @Value("${MYSQL_PASSWORD}")
    private String password;
     

    @Bean ("defaultTableResumeStrategyConfigurationBuilder")
    public TableResumeStrategyConfigurationBuilder getDefaultTableResumeStrategyConfigurationBuilder() throws PropertyVetoException {

        String endpointPath = FileUtil.normalizePath(
            consumerDirectory.endsWith(String.valueOf(File.separatorChar)) ? 
            consumerDirectory : consumerDirectory + String.valueOf(File.separatorChar));

        String filePath = endpointPath + consumerFile;
                
         return TableResumeStrategyConfigurationBuilder.newBuilder()
                .withDataSource(dataSource())                
                .withFilePath(filePath)                
                .withResumeCache(ResumeCache());        
    }
    
    @Bean
    public ResumeCache<File> ResumeCache() {
        return new CaffeineCache<>(1);
    }

    @Bean ("resumeDatasource")
    public DataSource dataSource() throws PropertyVetoException{
        ComboPooledDataSource dataSource = new ComboPooledDataSource();        
        dataSource.setDriverClass(gwhJpaCoreProperties.getClassName());        
        dataSource.setJdbcUrl(gwhJpaCoreProperties.getUrl());
        dataSource.setUser(gwhJpaCoreProperties.getUserName());        
        dataSource.setPassword(password);
        dataSource.setMinPoolSize(gwhJpaCoreProperties.getMinPoolSize());
        dataSource.setMaxPoolSize(gwhJpaCoreProperties.getMaxPoolSize());
        dataSource.setAcquireIncrement(gwhJpaCoreProperties.getIncrement());
        dataSource.setMaxIdleTime(gwhJpaCoreProperties.getMaxIdleTime());
        dataSource.setAcquireRetryDelay(gwhJpaCoreProperties.getRetryDelay());
        dataSource.setAcquireRetryAttempts(Integer.MAX_VALUE);
        dataSource.setAutoCommitOnClose(false);
        return dataSource;
    }
}
