package com.pw.resumecameldemo.resume;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.resume.Cacheable;
import org.apache.camel.resume.Deserializable;
import org.apache.camel.resume.Offset;
import org.apache.camel.resume.OffsetKey;
import org.apache.camel.resume.Resumable;
import org.apache.camel.resume.ResumeAdapter;
import org.apache.camel.resume.ResumeStrategyConfiguration;
import org.apache.camel.spi.annotations.JdkService;
import org.apache.camel.support.resume.Resumables;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JdkService("table-resume-strategy")
public class TableResumeStrategyImpl implements TableResumeStrategy, CamelContextAware { 

    private JdbcTemplate jdbcTemplate;

    private ResumeAdapter adapter;
    private TableResumeStrategyConfiguration resumeStrategyConfiguration;
    private final ReentrantLock writeLock = new ReentrantLock();    
    private CamelContext camelContext;
        

    public TableResumeStrategyImpl() {
    }
    
    public TableResumeStrategyImpl(TableResumeStrategyConfiguration resumeStrategyConfiguration) {
        this.resumeStrategyConfiguration = resumeStrategyConfiguration;
    }
     
    @Override
    public void setAdapter(ResumeAdapter adapter) {
        this.adapter = adapter; 
    }

    @Override
    public ResumeAdapter getAdapter() {
        if (adapter == null) {
            waitForInitialization();
        }

        return adapter;
    }

    private void waitForInitialization() {
        if (adapter == null) {
            try {
                log.info("Waiting for initialization");
                writeLock.lock();
                log.info("Initialization complete");
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override    
    public void loadCache() {
        if (!(adapter instanceof Deserializable)) {
            throw new RuntimeCamelException("Adapter must implement Deserializable");
        }

        // add select to load initial cache
        log.info("Load initial resume cache");
        Long offset = 0L;       

        try {
            offset = jdbcTemplate.queryForObject("SELECT IFNULL(resume_value, 0) FROM gwh.resume_strategy WHERE resume_key = " 
                                            + "'" + resumeStrategyConfiguration.getFilePath() + "'", Long.class);
        } catch (EmptyResultDataAccessException e) {
            log.info("No Resume Entry. Setting Offset to Zero");
            log.info("Adding resume offset to table");            
            jdbcTemplate.update("INSERT INTO gwh.resume_strategy (resume_key, resume_value) VALUES (?, ?)", 
                                            resumeStrategyConfiguration.getFilePath(), offset);
        }

        final File file = new File(resumeStrategyConfiguration.getFilePath());
        Resumable resumable = Resumables.of(file, offset);        
        doAdd(resumable.getOffsetKey(), resumable.getLastOffset());
        
    }

    @Override
    public <T extends Resumable> void updateLastOffset(T offset) throws Exception {
        updateLastOffset(offset, null);
    }

    @Override
    public <T extends Resumable> void updateLastOffset(T offset, UpdateCallBack updateCallBack) throws Exception {
        OffsetKey<?> offsetKey = offset.getOffsetKey();
        Offset<?> offsetValue = offset.getLastOffset();

        if (log.isDebugEnabled()) {
            log.debug("Updating last offset for key {} with value {}", offsetKey, offsetValue);
        }

        updateLastOffset(offsetKey, offsetValue);
    }

    @Override
    public void updateLastOffset(OffsetKey<?> offsetKey, Offset<?> offset) throws Exception {
        updateLastOffset(offsetKey, offset, null);
    }

    @Override
    @Transactional
    public void updateLastOffset(OffsetKey<?> offsetKey, Offset<?> offset, UpdateCallBack updateCallBack)
            throws Exception {
                
        try {
            writeLock.lock();
                        
            log.info("Update resume offset to table and cache");
            jdbcTemplate.update("UPDATE gwh.resume_strategy SET resume_value = ? WHERE resume_key = ?", 
                                            (long)offset.getValue(), offsetKey.getValue().toString());
            
        } finally {
            writeLock.unlock();
        }
        doAdd(offsetKey, offset);
         
    }

    protected void doAdd(OffsetKey<?> offsetKey, Offset<?> offsetValue) {
        if (adapter instanceof Cacheable) {
            Cacheable cacheable = (Cacheable) adapter;
            cacheable.add(offsetKey, offsetValue);
        }
    }

    @Override
    public void setResumeStrategyConfiguration(ResumeStrategyConfiguration resumeStrategyConfiguration) {
        if (resumeStrategyConfiguration instanceof TableResumeStrategyConfiguration) {
            this.resumeStrategyConfiguration = (TableResumeStrategyConfiguration) resumeStrategyConfiguration;
        } else {
            throw new RuntimeCamelException("ResumeStrategyConfiguration must be of type TableResumeStrategyConfiguration");
        }
    }

    @Override
    public ResumeStrategyConfiguration getResumeStrategyConfiguration() {
        return resumeStrategyConfiguration;
    }

    private void createJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(resumeStrategyConfiguration.getDataSource());            
        }        
    }

    @Override
    public void build() {
        // NO-OP
    }

    @Override
    public void init() {
        log.debug("Initializong table resume strategy");
    }

    @Override
    public void start() {
        log.info("Starting table resume strategy");
        createJdbcTemplate();
    }

    @Override
    public void stop() {
        log.debug("Stopping table resume strategy");
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }
    
}
