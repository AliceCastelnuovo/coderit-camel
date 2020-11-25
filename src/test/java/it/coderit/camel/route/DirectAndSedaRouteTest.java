package it.coderit.camel.route;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.coderit.camel.model.Location;


public class DirectAndSedaRouteTest extends CamelTestSupport {

	private final String from = "target/input";
	private final String to = "target/output";
	private final String url = "api.openweathermap.org/data/2.5";
	private final String path = "weather";

	private final String message1 = "	{\r\n" + "        \"id\": 3178229,\r\n" + "        \"name\": \"Como\",\r\n"
			+ "        \"state\": \"\",\r\n" + "        \"country\": \"IT\",\r\n" + "        \"coord\": {\r\n"
			+ "            \"lon\": 9.08744,\r\n" + "            \"lat\": 45.809978\r\n" + "        }\r\n" + "    }";
	
	private final String message2 = "{\"id\":\"7669118\",\"name\":\"Paraparaumu North\",\"state\":\"\",\"country\":\"NZ\",\"coord\":{\"lon\":\"175.018387\",\"lat\":\"-40.90551\"}}";

	final String response = "{\"coord\":{\"lon\":9.09,\"lat\":45.81},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":286.07,\"feels_like\":284.52,\"temp_min\":285.37,\"temp_max\":287.59,\"pressure\":1030,\"humidity\":69},\"visibility\":10000,\"wind\":{\"speed\":1.34,\"deg\":282,\"gust\":1.79},\"clouds\":{\"all\":0},\"dt\":1604911727,\"sys\":{\"type\":3,\"id\":2036041,\"country\":\"IT\",\"sunrise\":1604902532,\"sunset\":1604937572},\"timezone\":3600,\"id\":3178229,\"name\":\"Como\",\"cod\":200}";

	@Override
	@Before
	public void setUp() throws Exception {
		deleteDirectory(from);
		deleteDirectory(to);
		super.setUp();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() throws Exception {

		return new DirectRouteBuilder();

	}

	@Override
	public boolean isUseAdviceWith() {
		return true;
	}


	@Override
	protected Properties useOverridePropertiesWithPropertiesComponent() {

		Properties properties = new Properties();
		properties.put("file.fromPath", from);
		properties.put("file.toPath", to);
		properties.put("weather.api.url", url);
		properties.put("weather.api.path", path);
		properties.put("weather.api.key", "test");

		return properties;
	}

	@Test
	public void shouldProcessLocationObject() throws Exception {

		final String httpMockEndpoint = "mock:httpMocked";

		//Advice route
		AdviceWithRouteBuilder.adviceWith(context, "ask-weather", Boolean.FALSE,
				a -> {
					
					a.interceptSendToEndpoint("https:*")
						.skipSendToOriginalEndpoint()
						.to(httpMockEndpoint);
				
					a.mockEndpoints("file:" + to);
				}
						);
		
		
		// Start camelContext
		context.start();

		Location location1 = new ObjectMapper().readValue(message1, Location.class);
		Location location2 = new ObjectMapper().readValue(message2, Location.class);
		
		MockEndpoint http = getMockEndpoint(httpMockEndpoint);
		http.expectedMessageCount(2);
		http.expectedBodiesReceivedInAnyOrder(location1,location2);

		http.whenAnyExchangeReceived(new Processor() {
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setBody(response);
			}
		});

		MockEndpoint file = getMockEndpoint("mock:file:" + to);
		file.expectedMessageCount(2);
		file.expectedHeaderValuesReceivedInAnyOrder(Exchange.FILE_NAME, location1.getName() + "_weather.json",location2.getName() + "_weather.json");
		file.expectedBodiesReceivedInAnyOrder(response,response);

		template.sendBody("direct:weather", location1);
		template.sendBody("file:"+from, message2);
		
		
		assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);


	}
	
}
