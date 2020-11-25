package it.coderit.camel.route;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


public class AggregatorRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("file:/camelTest/cities?noop=true")
		.aggregate(constant(true),new AggregationStrategy() {
			
			@Override
			public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
				
				if (oldExchange == null) 
				{ return newExchange; }
			
				String oldBody = oldExchange.getIn().getBody(String.class);
				String newBody = newExchange.getIn().getBody(String.class);
				String body = oldBody + ",\n" + newBody;
				oldExchange.getIn().setBody(body);
				return oldExchange; 

				
			}
		})
		.completionSize(10)
		.completionInterval(1000)
		.setBody(body().prepend("[").append("]"))
		.setHeader(Exchange.FILE_NAME,simple("${id}-cities.json"))
		.to("file:/camelTest/output")
		.log("File ${header.CamelFileName} created. AggregationCompletedBy: ${exchangeProperty.CamelAggregatedCompletedBy}");
		


	}

}
