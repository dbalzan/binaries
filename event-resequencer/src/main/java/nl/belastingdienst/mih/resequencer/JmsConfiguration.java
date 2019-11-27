package nl.belastingdienst.mih.resequencer;

import org.springframework.context.annotation.Configuration;

/**
 * Configure a transactional activemq endpoint
 * 
 * @author David
 *
 */
@Configuration
public class JmsConfiguration {
	
//	@Bean
//	public ConnectionFactory jmsConnectionFactory() {
////		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
////		redeliveryPolicy.setMaximumRedeliveries(-1);
////		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
////		factory.setBrokerURL(activeMqBaseUri);
////		factory.setRedeliveryPolicy(redeliveryPolicy);
////		
////		return factory;
//	}
	
//	@Bean
//	public JmsTransactionManager jmsTransactionManager(final ConnectionFactory connectionFactory) {
////		JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
////		jmsTransactionManager.setConnectionFactory(connectionFactory);
////		return jmsTransactionManager;
//	}
//
//	@Bean
//	public JmsComponent jmsComponent(final ConnectionFactory connectionFactory,
//			final JmsTransactionManager jmsTransactionManager) {
////		ActiveMQComponent activeMQComponent = new ActiveMQComponent();
////		activeMQComponent.setConnectionFactory(connectionFactory);
////		activeMQComponent.setTransactionManager(jmsTransactionManager);
////		//activeMQComponent.setAcknowledgementModeName("CLIENT_ACKNOWLEDGE");
////		activeMQComponent.setTransacted(true);
////		return activeMQComponent;
//	}
//	
////	@Bean(name = "sjms")
////	public SjmsComponent buildSjmsComponent() {
////		SjmsComponent component = new SjmsComponent();
////		component.setConnectionFactory(new ActiveMQConnectionFactory(env.getProperty("activemq.baseuri")));
////		camelContext.addComponent("sjmsComponent", component);
////		return component;
////	}
}