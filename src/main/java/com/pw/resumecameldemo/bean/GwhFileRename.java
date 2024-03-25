package com.pw.resumecameldemo.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component("gwhFileRename")
public class GwhFileRename {
    
    public String rename(String fileName) {

        DateFormat dt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        String formattedDate = dt.format(date);

        return fileName + "." + formattedDate;
    }

}
