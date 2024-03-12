package com.pw.resumecameldemo.route;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.benmanes.caffeine.cache.Cache;
import com.pw.resumecameldemo.model.ResumeRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileResumeUpdateRoutePolicy extends RoutePolicySupport {

    @Autowired
    @Qualifier("resumeJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("resumeCache")    
    Cache<String, ResumeRecord> resumeCache;

    private final ReentrantLock writeLock = new ReentrantLock();

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        
        log.info("Beginning Route: {}", route.getId());
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        
        if (!exchange.isFailed()) {
            // update offset and count for file from respective properties
            // long count = exchange.getProperty("gwhResumeCount", long.class);
            // long offset = exchange.getProperty("gwhResumeOffset", long.class);
            ResumeRecord resumeRecord = resumeCache.get(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), k -> ResumeRecord.get());

            long lastRecord = resumeRecord.getCurrentRecord();

            log.info("Updated datastore last record: {} and last offset: {} and current record: {}", 
                    lastRecord, resumeRecord.getLastOffset(), resumeRecord.getCurrentRecord());

            try {

                writeLock.lock();

                jdbcTemplate.update("UPDATE gwh.resume_strategy SET last_record = ?, last_offset = ?, current_record = ? WHERE resume_key = ?", 
                        lastRecord, resumeRecord.getLastOffset(), resumeRecord.getCurrentRecord(), exchange.getIn().getHeader(Exchange.FILE_NAME, String.class));

                resumeRecord.setLastRecord(lastRecord);
                resumeCache.put(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), resumeRecord);


            } finally {
                writeLock.unlock();
            }
        }
    }

    public void updateLineCount(Exchange exchange) {
        
        final String body = exchange.getMessage().getBody(String.class);

        ResumeRecord resumeRecord = resumeCache.get(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), k -> ResumeRecord.get());
        long lastRecord = resumeRecord.getLastRecord();
        long lastOffset = resumeRecord.getLastOffset();
        long currentRecord = resumeRecord.getCurrentRecord();
                
        lastOffset = lastOffset + body.length() + 1;        
        currentRecord++;
                
        resumeRecord.setLastOffset(lastOffset);
        resumeRecord.setCurrentRecord(currentRecord);
        resumeCache.put(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), resumeRecord);
                        
        log.info("Update Cache last record: {} / last offset: {} / current record: {} ", lastRecord, lastOffset, currentRecord);        
    }


    public boolean skipRecord(Exchange exchange) {

        ResumeRecord resumeRecord = resumeCache.get(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), k -> ResumeRecord.get());

        if ((resumeRecord.getCurrentRecord()) < resumeRecord.getLastRecord()) {
            log.info("Skipping Record: last record: {} / current record: {} ", resumeRecord.getLastRecord(), resumeRecord.getCurrentRecord());        
            return true;
        }
        return false;

    }


}