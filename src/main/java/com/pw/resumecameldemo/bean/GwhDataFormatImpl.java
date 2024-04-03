package com.pw.resumecameldemo.bean;

import org.apache.camel.dataformat.flatpack.FlatpackDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.configuration.GwhFlatpackConfiguration;
import com.pw.resumecameldemo.loaders.GwhDataLoader;

@Component
public class GwhDataFormatImpl implements GwhDataFormat {

    @Autowired
    @Qualifier("gwhDataFileConfigLoader")
    GwhDataLoader<GwhFlatpackConfiguration> dataLoader;

    GwhFlatpackConfiguration dataFormatConfig = null;

    @Override
    public DataFormat geDataFormat(String formatName) {
        if (dataFormatConfig == null) {
            dataFormatConfig = loadDataFormatConfiguration(formatName);
        }

        FlatpackDataFormat dataFormat = new FlatpackDataFormat();
        dataFormat.setDefinition(dataFormatConfig.getFormatDefinition());
        dataFormat.setFixed(dataFormatConfig.isFixedFile());
        dataFormat.setDelimiter(dataFormatConfig.getDelimiter());
        dataFormat.setTextQualifier(dataFormatConfig.getTextQualifier());
        dataFormat.setIgnoreFirstRecord(dataFormatConfig.isIgnoreFirstRecord());
        return dataFormat;
    }

    private GwhFlatpackConfiguration loadDataFormatConfiguration(String formatName) {

        GwhFlatpackConfiguration dataFormatConfig = new GwhFlatpackConfiguration();
        dataFormatConfig = dataLoader.load(formatName);
        return dataFormatConfig;

    }

}
