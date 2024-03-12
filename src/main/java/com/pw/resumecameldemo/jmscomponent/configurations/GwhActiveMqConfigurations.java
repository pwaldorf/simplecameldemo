package com.pw.resumecameldemo.jmscomponent.configurations;


import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "gwh.framework.component.activemqtest.enabled", havingValue = "true", matchIfMissing = false)
public class GwhActiveMqConfigurations {

    @Autowired
    GwhActiveMqProperties gwhActiveMqProperties;

    @Bean("activeMqTestConnectionFactory")
	public ConnectionFactory activeMqTestConnectionFactory() {		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

		try {
			log.debug("Set Connection Parms");
			connectionFactory.setBrokerURL(gwhActiveMqProperties.getBrokerUrl());
			connectionFactory.setUser(gwhActiveMqProperties.getUsername());
			connectionFactory.setPassword(gwhActiveMqProperties.getPassword());			
			log.debug("Connection Parms Set");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		WrapCachingConnectionFactory wrapConnectionFactory = new WrapCachingConnectionFactory(connectionFactory);
		wrapConnectionFactory.setSessionCacheSize(gwhActiveMqProperties.getSessionCacheSize());
		
		return wrapConnectionFactory;
	}    

    @Bean("activeMqTestTransactionManager")
    public PlatformTransactionManager activeMqTestTransactionManager() {
        JmsTransactionManager transactionManager = new JmsTransactionManager(activeMqTestConnectionFactory());
        return transactionManager;
    }

    @Bean("txRequiredActiveMqTest")
    public SpringTransactionPolicy txRequiredActiveMqTest() {
        SpringTransactionPolicy required = new SpringTransactionPolicy();
        required.setTransactionManager(activeMqTestTransactionManager());
        required.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return required;    
    }

    @Bean("txRequiredNewActiveMqTest")
    public SpringTransactionPolicy txRequiredNewActiveMqTest() {
        SpringTransactionPolicy required = new SpringTransactionPolicy();
        required.setTransactionManager(activeMqTestTransactionManager());
        required.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
        return required;    
    }

    @Bean("txRequiredMandatoryActiveMqTest")
    public SpringTransactionPolicy txRequiredMandatoryActiveMqTest() {
        SpringTransactionPolicy required = new SpringTransactionPolicy();
        required.setTransactionManager(activeMqTestTransactionManager());
        required.setPropagationBehaviorName("PROPAGATION_REQUIRES_Mandatory");
        return required;    
    }
}
