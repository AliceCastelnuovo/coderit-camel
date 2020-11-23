package it.coderit.camel.route;

import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import it.coderit.camel.utils.JsonHelper;

public class BeanRouteBuilderTest extends CamelTestSupport {

	private final String from = "target/input";
	private final String to = "target/output";


	@Override
	@Before
	public void setUp() throws Exception {
		deleteDirectory(from);
		deleteDirectory(to);
		super.setUp();
	}

	@Override
	public String isMockEndpoints() {
		return "file:" + to;
	}

    @Override
    protected CamelContext createCamelContext() throws Exception
    {
        CamelContext camelContext = new DefaultCamelContext();
        
		Properties properties = new Properties();
		properties.put("file.fromPath", from);
		properties.put("file.toPath", to);
        
		PropertiesComponent propComponent = new PropertiesComponent();
        propComponent.setOverrideProperties(properties);

        camelContext.setPropertiesComponent(propComponent);
        camelContext.addRoutes(new BeanRouteBuilder());
  		camelContext.getRegistry().bind("jsonHelper", new JsonHelper());
        
        return camelContext;
    }

	@Test
	public void testMockFileEndpoints() throws Exception {

		final String message1 = "{\"id\":\"3178229\",\"name\":\"Como\",\"state\":\"\",\"country\":\"IT\",\"coord\":{\"lon\":\"9.08744\",\"lat\":\"45.809978\"}}";
		final String message2 = "{\"id\":\"3172391\",\"name\":\"Napoli\",\"state\":\"\",\"country\":\"IT\",\"coord\":{\"lon\":\"14.41667\",\"lat\":\"40.883331\"}}";
		
		MockEndpoint file = getMockEndpoint("mock:file:" + to);
		file.expectedMessageCount(2);
		file.expectedBodiesReceivedInAnyOrder("Como","Napoli");
		
		template.sendBodyAndHeader("file:" + from, message1, Exchange.FILE_NAME, "como.txt");
		template.sendBodyAndHeader("file:" + from, message2, Exchange.FILE_NAME, "napoli.txt");
		
		assertMockEndpointsSatisfied();

	}
}
