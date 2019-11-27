package nl.belastingdienst.mih.resequencer.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import nl.belastingdienst.mih.resequencer.processor.SequenceRepository;

@Component
public class ResequencerRoute extends RouteBuilder {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Value(value = "${uri.unordered}")
	private String unorderedUri;

	@Value(value = "${uri.ordered}")
	private String orderedUri;

	@Value(value = "${uri.retry}")
	private String retryUri;
	
	@Value(value = "${timer.retry}")
	private String retryTimer;
	
	@Value(value = "${header.key}")
	private String keyHeader;

	@Value(value = "${header.sequence}")
	private String sequenceHeader;
	
	@Autowired
	private SequenceRepository sequenceRespository;
	
	@Override
	public void configure() throws Exception {

		from(unorderedUri)
				// Name the route
				.routeId("PrimaryRoute")
				// Extract the key and sequence
				.setProperty("key", header(keyHeader))
				.setProperty("sequence", header(sequenceHeader))
				// Log for debugging purposes
				.log(LoggingLevel.INFO, log, "Processing message ${exchangeProperty.key}|${exchangeProperty.sequence}")
				// Process the event
				.to("direct:processEvent");

		from(retryTimer)
				// Poll the retry queue
				.pollEnrich(retryUri)
				// Name the route for debugging purposes
				.routeId("RetryRoute")
				// Extract the key and sequence
				.setProperty("key", header(keyHeader))
				.setProperty("sequence", header(sequenceHeader))
				// Log for debugging purposes
				.log(LoggingLevel.INFO, log, "Retrying message ${exchangeProperty.key}|${exchangeProperty.sequence}")
				// Process the event
				.to("direct:processEvent");

		from("direct:processEvent")
			// Check whether this is the correct next sequence number
			.choice().when(method(sequenceRespository, "check"))
			// Send to ordered endpoint
			.to(orderedUri)
			// Update if successful
			.bean(sequenceRespository, "update")
			// Otherwise if it is not the next sequence
			.otherwise()
			// Send to the retry endpoint
			.to(retryUri)
			// End Processing
			.end();
	}

}
