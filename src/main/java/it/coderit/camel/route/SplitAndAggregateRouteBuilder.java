package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplitAndAggregateRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		
		from("file:/camelTest?noop=true&filter=cities.txt")
			.unmarshal("locationDataFormat")
			.split(body(), AggregationStrategies.string().delimiter(","))
				.setHeader("name",simple("${body.name}"))
				.to("direct:weather")
			.end()
			.setBody(simple("[${body}]"))
			.setHeader(Exchange.FILE_NAME,simple("aggr_weather.json"))
			.to("file:/camelTest/output")
			.log("File ${header.CamelFileName} created");
				
		
		from("direct:weather")
			.routeId("weather-api")
			.setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body.id}"))
				.setExchangePattern(ExchangePattern.InOut)
			.to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
			.log(LoggingLevel.DEBUG, "${body}")
			;
	}

}
