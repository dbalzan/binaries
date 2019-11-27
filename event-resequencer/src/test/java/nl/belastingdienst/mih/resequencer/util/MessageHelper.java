package nl.belastingdienst.mih.resequencer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;

public class MessageHelper {

	private CamelContext context;
	private ProducerTemplate producerTemplate;
	private List<Exchange> messageList = new ArrayList<Exchange>();

	public MessageHelper(CamelContext context, ProducerTemplate producerTemplate) {
		super();
		this.context = context;
		this.producerTemplate = producerTemplate;
	}

	public void createMessage(String channel, long sequence, int expectedOrder) {
		Exchange e = new DefaultExchange(context);
		e.getIn().setHeader("BTW", channel);
		e.getIn().setHeader("seqNo", sequence);
		e.getIn().setHeader("expectedOrder", expectedOrder);
		e.getIn().setBody(channel + "|" + sequence);
		
		messageList.add(e);
	}

	public void sendMessages() {
		messageList.stream().forEach(m -> producerTemplate.send("direct:unorderedEndpoint", m));
	}

	public List<Exchange> getMessageList() {
		return messageList;
	}

	public List<Object> getSortedBodyList() {
		// Sort by expectedOrder into a new list
		List<Exchange> sortedBodyList = messageList.stream().sorted((m1, m2) -> {
		    return (((Integer)m1.getIn().getHeader("expectedOrder")).compareTo((Integer)m2.getIn().getHeader("expectedOrder")));
		}).collect(Collectors.toList());
		// Now just return the bodies
		return sortedBodyList.stream().map(Exchange::getIn).map(Message::getBody).collect(Collectors.toList());
	}

}
