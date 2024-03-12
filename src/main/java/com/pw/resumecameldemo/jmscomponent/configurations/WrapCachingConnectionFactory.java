package com.pw.resumecameldemo.jmscomponent.configurations;

import org.springframework.core.InfrastructureProxy;
import org.springframework.jms.connection.CachingConnectionFactory;
import jakarta.jms.ConnectionFactory;

public class WrapCachingConnectionFactory extends CachingConnectionFactory implements InfrastructureProxy {

    @Override
    public Object getWrappedObject() {
        return this.getTargetConnectionFactory();
    } 
    
    public WrapCachingConnectionFactory(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    } 
    
    public WrapCachingConnectionFactory() {
        
    }       
    
}
