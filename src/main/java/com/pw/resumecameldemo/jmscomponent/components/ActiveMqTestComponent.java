package com.pw.resumecameldemo.jmscomponent.components;

import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "gwh.framework.component.activemqtest.enabled", havingValue = "true", matchIfMissing = false)
public class ActiveMqTestComponent {

    @Autowired
    private ConnectionFactory activeMqTestConnectionFactory;

    @Autowired
    private PlatformTransactionManager activeMqTestTransactionManager;

    
    @Bean("activeMqTestConsumerTx")
    public JmsComponent activeMqTestConsumerTransacted() throws Exception {
              
        log.debug("jmsConsumerTransacted Component Creation");
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(activeMqTestConnectionFactory);
        jmsComponent.setTransacted(true);
        jmsComponent.setCacheLevelName("CACHE_CONSUMER");                
        jmsComponent.setTransactionManager(activeMqTestTransactionManager);
        return jmsComponent;
    }

    @Bean("activeMqTestProducerTx")
    public JmsComponent activeMqTestProducerTransacted() throws Exception {
              
        log.debug("jmsProducerTransacted Component Creation");
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(activeMqTestConnectionFactory);
        jmsComponent.setTransacted(true);
        jmsComponent.setCacheLevelName("CACHE_NONE");
        jmsComponent.setTransactionManager(activeMqTestTransactionManager);
        jmsComponent.setHeaderFilterStrategy(activeMqTestHeaderFilterStrategy());
        return jmsComponent;
    }

    @Bean("activeMqTestHeaderFilterStrategy")
    public DefaultHeaderFilterStrategy activeMqTestHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy headerFilterStrategy = new DefaultHeaderFilterStrategy();
        headerFilterStrategy.setOutFilterPattern("kafka.*");
        return headerFilterStrategy;
    }

}
