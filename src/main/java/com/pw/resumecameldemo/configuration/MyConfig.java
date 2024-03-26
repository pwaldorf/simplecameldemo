package com.pw.resumecameldemo.configuration;

import java.beans.PropertyVetoException;
import java.net.URI;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.component.caffeine.processor.idempotent.CaffeineIdempotentRepository;
import org.apache.camel.component.file.remote.FtpEndpoint;
import org.apache.camel.routepolicy.quartz.CronScheduledRoutePolicy;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.camel.spi.UriEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pw.resumecameldemo.bean.MyExceptionTester;
import com.pw.resumecameldemo.exception.GwhFileExceptionHandler;
import com.pw.resumecameldemo.model.ResumeRecord;

import jakarta.jms.MessageFormatException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MyConfig {

    @Autowired
    GwhJpaCoreProperties gwhJpaCoreProperties;

    @Autowired
    CamelContext camelContext;

    @Value("${gwh.ftp.directory}")
    private String consumerDirectory;

    @Value("${gwh.ftp.filename}")
    private String consumerFile;

    @Value("${MYSQL_PASSWORD}")
    private String password;


    @Bean ("gwhExceptionType")
    public Class<? extends Throwable> gwhExceptionType() {
        return MessageFormatException.class;
    }


    @Bean ("myExceptionTester")
    public MyExceptionTester myExceptionTester() {
        return new MyExceptionTester();
    }

    @Bean ("gwhFileExceptionHandler")
    public GwhFileExceptionHandler gwhFileExceptionHandler() {
        return new GwhFileExceptionHandler(camelContext.createProducerTemplate());
    }

    @Bean("cronScheduledRoutePolicy")
    public CronScheduledRoutePolicy cronScheduledRoutePolicy() {
        CronScheduledRoutePolicy cronScheduledRoutePolicy = new CronScheduledRoutePolicy();
        cronScheduledRoutePolicy.setRouteStartTime("1 * * * * ?");
        return cronScheduledRoutePolicy;
    }

    @Bean ("fileIdempotentRepository")
    public CaffeineIdempotentRepository fileIdempotentRepository() {
        return new CaffeineIdempotentRepository("fileRecords");
    }

    @Bean ("recordIdempotentRepository")
    public CaffeineIdempotentRepository recordIdempotentRepository() {

        return new CaffeineIdempotentRepository("recordRecords");
    }

    @Bean("resumeCache")
    public Cache<String, ResumeRecord> ResumeCache() {
        return Caffeine.newBuilder().maximumSize(1).build(k -> ResumeRecord.get());
    }

    @Bean
    public JdbcTemplate resumeJdbcTemplate() throws PropertyVetoException {
        return new JdbcTemplate(dataSource());
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
