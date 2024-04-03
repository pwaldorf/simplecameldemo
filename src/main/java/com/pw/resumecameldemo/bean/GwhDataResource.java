package com.pw.resumecameldemo.bean;

import org.springframework.stereotype.Component;

@Component("gwhDataResource")
public class GwhDataResource {

    public String getDefinitionDelimited() {
        return new StringBuilder("<?xml version='1.0'?>")
            .append("<!DOCTYPE PZMAP SYSTEM \"flatpack.dtd\" >")
            .append("<PZMAP>")
            .append("<RECORD id=\"header\" indicator=\"FIRSTNAME\" elementNumber=\"2\">")
            .append("<COLUMN name=\"HEADERDATA\" />")
            .append("</RECORD>")
            .append("<COLUMN name=\"FIRSTNAME\" />")
            .append("<COLUMN name=\"LASTNAME\" />")
            .append("<COLUMN name=\"ADDRESS\" />")
            .append("<COLUMN name=\"CITY\" />")
            .append("<COLUMN name=\"STATE\" />")
            .append("<COLUMN name=\"ZIP\" />")
            .append("</PZMAP>")
            .toString();
    }

    public String getDefinitionFixed() {
        return new StringBuilder("<?xml version='1.0'?>")
            .append("<!DOCTYPE PZMAP SYSTEM ")
            .append("\"flatpack.dtd\" >")
            .append("<PZMAP>")
            .append("<COLUMN name=\"FIRSTNAME\" length=\"35\" />")
            .append("<COLUMN name=\"LASTNAME\" length=\"35\" />")
            .append("<COLUMN name=\"ADDRESS\" length=\"100\" />")
            .append("<COLUMN name=\"CITY\" length=\"100\" />")
            .append("<COLUMN name=\"STATE\" length=\"2\" />")
            .append("<COLUMN name=\"ZIP\" length=\"5\" />")
            .append("</PZMAP>")
            .toString();
    }
}
