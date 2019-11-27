package nl.belastingdienst.mih.resequencer.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import nl.belastingdienst.mih.resequencer.Application;
import nl.belastingdienst.mih.resequencer.util.MessageHelper;

@SpringBootTest(classes = Application.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpointsAndSkip("http*")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SequencerRouteTest {

	@Autowired
	CamelContext context;

	@Autowired
	ProducerTemplate producerTemplate;

	@Autowired
	ResourceLoader resourceLoader;

	@EndpointInject(value = "mock:jms:sibEndpoint")
	private MockEndpoint mockSibEndpoint;

	@Test
	public void testOneKeyIncorrectSequence() throws InterruptedException  {
		MessageHelper helper = new MessageHelper(context, producerTemplate);
		helper.createMessage("A", 1L, 1);
		helper.createMessage("A", 3L, 3);
		helper.createMessage("A", 2L, 2);
				
		mockSibEndpoint.expectedMessageCount(3);
		mockSibEndpoint.expectsAscending().header("seqNo");
		
		helper.sendMessages();
		
		mockSibEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void testTwoKeysCorrectSequence() throws InterruptedException {
		MessageHelper helper = new MessageHelper(context, producerTemplate);
		helper.createMessage("A", 1L, 1);
		helper.createMessage("B", 100L, 2);
		helper.createMessage("A", 2L, 3);
		helper.createMessage("B", 101L, 4);
		
		mockSibEndpoint.expectedMessageCount(4);
		mockSibEndpoint.expectedBodiesReceived(helper.getSortedBodyList());
		
		helper.sendMessages();
		
		mockSibEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void testTwoKeysIncorrectSequence() throws InterruptedException {
		MessageHelper helper = new MessageHelper(context, producerTemplate);
		helper.createMessage("A", 1L, 1);
		helper.createMessage("B", 100L, 2);
		helper.createMessage("A", 3L, 5);
		helper.createMessage("B", 102L, 6);
		helper.createMessage("A", 2L, 3);
		helper.createMessage("B", 101L, 4);
		
		mockSibEndpoint.expectedMessageCount(6);
		mockSibEndpoint.expectedBodiesReceived(helper.getSortedBodyList());
		
		helper.sendMessages();
		
		mockSibEndpoint.assertIsSatisfied();
	}

}
