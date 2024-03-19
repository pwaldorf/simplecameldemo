package com.pw.resumecameldemo.route;

import org.apache.camel.support.RoutePolicySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.pw.resumecameldemo.model.ResumeRecord;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.component.caffeine.processor.idempotent.CaffeineIdempotentRepository;


@Slf4j
@Component("fileResumeRoutePolicy")
public class FileResumeRoutePolicy extends RoutePolicySupport {

    @Autowired
    @Qualifier("resumeJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("resumeCache")
    Cache<String, ResumeRecord> resumeCache;

    @Autowired
    @Qualifier("recordIdempotentRepository")
    CaffeineIdempotentRepository recordIdempotentRepository;

    private final ReentrantLock writeLock = new ReentrantLock();

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {

        // Lookup offset and count for file and add to properties        
        log.info("Beginning Route : {}", route.getId());

        // Lookup offset and count for file and add to properties
        String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
        
        ResumeRecord resumeRecord = null;

        String sql = "SELECT last_record, last_offset, current_record FROM gwh.resume_strategy WHERE resume_key = ?" ;

        RowMapper<ResumeRecord> rowMapper = new RowMapper<ResumeRecord>() {
            public ResumeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResumeRecord resumeRecord = new ResumeRecord();
                resumeRecord.setLastRecord(rs.getLong("last_record"));
                resumeRecord.setLastOffset(rs.getLong("last_offset"));
                resumeRecord.setCurrentRecord(rs.getLong("current_record"));
                return resumeRecord;
            }
        };        

        try {
            resumeRecord = jdbcTemplate.queryForObject(sql, rowMapper, fileName);

        } catch (EmptyResultDataAccessException e) {
            log.info("No Resume Entry. Setting Offset to Zero");
            log.info("Adding resume offset to table");            
            resumeRecord = new ResumeRecord();
            resumeRecord.setLastRecord(0L);
            resumeRecord.setLastOffset(0L);
            resumeRecord.setCurrentRecord(0L);
            jdbcTemplate.update("INSERT INTO gwh.resume_strategy (resume_key, last_record, last_offset, current_record) VALUES (?, ?, ?, ?)", 
                                    fileName, resumeRecord.getLastRecord(), resumeRecord.getLastOffset(), resumeRecord.getCurrentRecord());            
            
        }         

        if (resumeRecord != null) {
            resumeRecord.setCurrentRecord(0L);
            log.info("Setting Route Last Record: {} and Last Offset: {} and Current Record: {}", 
                        resumeRecord.getLastRecord(), resumeRecord.getLastOffset(), resumeRecord.getCurrentRecord());
            resumeCache.put(fileName, resumeRecord);
        }        

    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        
        if (!exchange.isFailed()) {
            // reset offset and count for file back to zero            
            String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);            
            try {

                writeLock.lock();
                            
                log.info("Reset resume offset to table and cache");
                jdbcTemplate.update("UPDATE gwh.resume_strategy SET last_record = ?, last_offset= ?, current_record = ? WHERE resume_key = ?", 
                                                 0L, 0L, 0L, fileName);

                resumeCache.invalidateAll();
                //recordIdempotentRepository.clear();
                
            } finally {
                writeLock.unlock();
            }
        }

        this.stopRouteAsync(route);
        
        
    }


}
