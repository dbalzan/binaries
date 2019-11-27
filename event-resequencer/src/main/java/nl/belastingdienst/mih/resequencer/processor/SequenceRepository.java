package nl.belastingdienst.mih.resequencer.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 * @author David
 *
 */
@Component
public class SequenceRepository {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	// Key store to hold the latest sequence of last received messages
	private Map<String, Long> store = new ConcurrentHashMap<String, Long>();
	
	public boolean check(Exchange e) {
		String key = (String) e.getProperty("key");
		Long thisSequence = (Long) e.getProperty("sequence");
		Long lastSequence = store.get(key);
		if (lastSequence == null) {
			log.debug("Message " + key + " has uninitialised sequence.  Found [" + thisSequence + "] as first sequence");
			return true;
		} else if (thisSequence.equals(lastSequence+1)) {
			log.debug("Message " + key + " has correct sequence [" + thisSequence + "]");
			return true;
		} else {
			log.info("Message " + key + " has incorrect sequence.  Expected [" + (lastSequence+1) + "] but found [" + thisSequence + "]");
			return false;
		}
	}
	
	public void update(Exchange e) {
		String key = (String) e.getProperty("key");
		Long thisSequence = (Long) e.getProperty("sequence");

		store.put(key, thisSequence);
	}

}
