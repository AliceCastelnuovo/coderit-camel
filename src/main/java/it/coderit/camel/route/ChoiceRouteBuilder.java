package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class ChoiceRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		
		from("file:/camelTest?noop=true&fileName=cities.txt")
			.unmarshal("locationDataFormat")
			.split(body())
				.setHeader("name",simple("${body.name}"))
				.to("direct:weather")
				.setHeader(Exchange.FILE_NAME,simple("${header.name}_weather.json"))
				.choice()
					.when().xpath("/current/city[country='IT']")
						.to("file:/camelTest/output/italy")
					.when().xpath("/current/city[country='FR']")
						.to("file:/camelTest/output/france")
					.otherwise()
						.to("file:/camelTest/output/others")
					//		.stop()
				.end()
				.to("file:/camelTest/output/all")
			.end();

		
		from("direct:weather")
			.routeId("weather-api")
			.setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body.id}&mode=xml"))
			.to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
			.convertBodyTo(String.class)
			.log(LoggingLevel.DEBUG, "${body}")
			;
	}

}
