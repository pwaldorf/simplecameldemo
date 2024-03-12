package com.pw.resumecameldemo.model;

public class ResumeRecord {

    private long lastRecord;
    private long lastOffset;
    private long currentRecord;
    
    public long getLastRecord() {
        return lastRecord;
    }

    public void setLastRecord(long lastRecord) {
        this.lastRecord = lastRecord;
    }

    public long getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(long lastOffset) {
        this.lastOffset = lastOffset;
    }

    public long getCurrentRecord() {
        return currentRecord;
    }

    public void setCurrentRecord(long currentRecord) {
        this.currentRecord = currentRecord;
    }

    public static ResumeRecord get() {
        ResumeRecord resumeRecord = new ResumeRecord();
        resumeRecord.setLastRecord(0);
        resumeRecord.setLastOffset(0);
        resumeRecord.setCurrentRecord(0);
        return resumeRecord;
    }
    
}
