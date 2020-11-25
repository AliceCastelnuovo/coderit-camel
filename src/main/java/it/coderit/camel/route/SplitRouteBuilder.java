package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class SplitRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		
		from("file:/camelTest?noop=true&fileName=cities.txt")
			.unmarshal("locationDataFormat")
			.split(body())
				.setHeader("name",simple("${body.name}"))
				.to("direct:weather")
				.setHeader(Exchange.FILE_NAME,simple("${header.name}_weather.json"))
				.to("file:/camelTest/output")
				.log("File ${header.CamelFileName} created")
				.log("Is the split completed? ${exchangeProperty.CamelSplitComplete}")
			.end();

		
		from("direct:weather")
			.routeId("weather-api")
			.setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body.id}"))
			.to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
			.log(LoggingLevel.DEBUG, "${body}")
			;
	}

}
