package com.pw.resumecameldemo.jmscomponent.configurations;


import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "gwh.framework.component.activemqtest.enabled", havingValue = "true", matchIfMissing = false)
public class GwhActiveMqConfigurations {

    @Autowired
    GwhActiveMqProperties gwhActiveMqProperties;

    @Bean("newFileJmsTemplate")
    public JmsTemplate newFileJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(activeMqTestConnectionFactory());
        //jmsTemplate.setSessionTransacted(true);
        //jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        return jmsTemplate;
    }

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

    // @Bean("txRequiredActiveMqTest")
    // public SpringTransactionPolicy txRequiredActiveMqTest() {
    //     SpringTransactionPolicy required = new SpringTransactionPolicy();
    //     required.setTransactionManager(activeMqTestTransactionManager());
    //     required.setPropagationBehaviorName("PROPAGATION_REQUIRED");
    //     return required;
    // }

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

    @Bean("routeChainedTransactionManager")
    public ChainedTransactionManager routeChainedTransactionManager() {
        return new ChainedTransactionManager(activeMqTestTransactionManager());
    }

    @Bean("txRequiredActiveMqTest")
    public SpringTransactionPolicy txRequiredActiveMqTest() {
        SpringTransactionPolicy required = new SpringTransactionPolicy();
        required.setTransactionManager(routeChainedTransactionManager());
        required.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return required;
    }

    @Bean
    public TransactionTemplate transactionTemplate (ChainedTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
