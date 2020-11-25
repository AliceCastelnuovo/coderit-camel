package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import it.coderit.camel.model.Location;



//api.openweathermap.org/data/2.5/group?id=524901,703448,2643743&appid={API key}
public class IdAggregatorRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("file:/camelTest/cities?noop=true")
		.streamCaching()
		.unmarshal().json(JsonLibrary.Jackson, Location.class)
				.setHeader("country",simple("${body.country}"))
				.log("${body}")
		.aggregate(header("country"),AggregationStrategies.string().delimiter(",").pick(simple("${body.id}")))
		.completionInterval(5000)
				.to("direct:weather")
		.setHeader(Exchange.FILE_NAME,simple("${header.country}_weather.json"))
		.to("file:/camelTest/output")
		.log("File ${header.CamelFileName} created");
		
		
		from("direct:weather")
			.routeId("weather-api")
			.setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body}"))
			.to("https:{{weather.api.url}}/group?httpMethod=GET")
			.log(LoggingLevel.DEBUG, "${body}")
			;
	}

}
