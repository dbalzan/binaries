package nl.belastingdienst.mih.resequencer.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple processor that logs the message body. Can be added to routes with a
 * breakpoint to enable debugging.
 * 
 * @author David
 *
 */
public class BreakpointDebugger implements Processor {

	Log logger = LogFactory.getLog(BreakpointDebugger.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		logger.info(exchange.getIn().getBody());

	}

}
