package com.pw.resumecameldemo.resume;

import javax.sql.DataSource;

import org.apache.camel.resume.ResumeStrategyConfiguration;

public class TableResumeStrategyConfiguration extends ResumeStrategyConfiguration {
    
    private DataSource dataSource;    
    private String filePath;
    
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String resumeStrategyService() {
        return "table-resume-strategy";
    }
    
}
