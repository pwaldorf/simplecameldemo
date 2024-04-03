package com.pw.resumecameldemo.loaders;

import com.pw.resumecameldemo.configuration.GwhFlatpackConfiguration;

public class GwhJpaDataFileDataLoader implements GwhDataLoader<GwhFlatpackConfiguration> {

    @Override
    public GwhFlatpackConfiguration load(String formatName) {

        GwhFlatpackConfiguration configuration = new GwhFlatpackConfiguration();
        configuration.setDelimiter(',');
        configuration.setTextQualifier('"');
        configuration.setFixedFile(false);
        configuration.setFormatDefinition("bean:gwhDataResource?method=getDefinitionDelimited");
        configuration.setIgnoreFirstRecord(false);
        return configuration;
    }

}
