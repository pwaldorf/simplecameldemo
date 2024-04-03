package com.pw.resumecameldemo.bean;


import org.apache.camel.spi.DataFormat;


public interface GwhDataFormat {

    DataFormat geDataFormat(String formatName);

}
