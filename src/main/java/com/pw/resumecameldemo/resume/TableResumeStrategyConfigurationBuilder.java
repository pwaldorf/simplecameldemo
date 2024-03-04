package com.pw.resumecameldemo.resume;

import javax.sql.DataSource;

import org.apache.camel.support.resume.BasicResumeStrategyConfigurationBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableResumeStrategyConfigurationBuilder  extends 
        BasicResumeStrategyConfigurationBuilder<TableResumeStrategyConfigurationBuilder, TableResumeStrategyConfiguration> {

    private DataSource dataSource;  
    private String filePath;    
    
    public TableResumeStrategyConfigurationBuilder() {
    }

    public TableResumeStrategyConfigurationBuilder withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public TableResumeStrategyConfigurationBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
    
    public TableResumeStrategyConfiguration build() {

        TableResumeStrategyConfiguration resumeStrategyConfiguration = new TableResumeStrategyConfiguration();
        buildCommonConfiguration(resumeStrategyConfiguration);  
        
        resumeStrategyConfiguration.setDataSource(dataSource);        
        resumeStrategyConfiguration.setFilePath(filePath);        

        return resumeStrategyConfiguration;
    }

    public static TableResumeStrategyConfigurationBuilder newBuilder() {
        return new TableResumeStrategyConfigurationBuilder();
    }
    
}
