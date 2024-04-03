package com.pw.resumecameldemo.bean;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.component.flatpack.DataSetList;
import org.apache.camel.dataformat.beanio.BeanIODataFormat;
import org.apache.camel.dataformat.flatpack.FlatpackDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.ResourceHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.sf.flatpack.DataSet;

@Slf4j
@Component("gwhMqSender1")
public class MqSender1 {

    public void send(Exchange exchange) throws IOException {

        //DataSetList dataSetList = exchange.getIn().getBody(DataSetList.class);
        //String[] cols = dataSetList.getColumns();

        // if (exchange.getIn().getBody(DataSetList.class).getErrorCount() > 1) {
        //     throw new RuntimeException(exchange.getIn().getBody(DataSetList.class).getErrors().toString());
        // }

        // InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(exchange.getContext(),"bean:gwhDataResource?method=getDefinition");
         log.info("is: ");

    }
}
