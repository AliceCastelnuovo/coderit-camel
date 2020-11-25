package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.xpath.XPathBuilder;

import com.jayway.jsonpath.JsonPathException;


public class PredicateRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		Predicate predicate = PredicateBuilder.and(XPathBuilder.xpath("/current/clouds[@value>20]"), XPathBuilder.xpath("/current/city[country='IT']"));
		
		from("file:/camelTest?noop=true&fileName=cities.txt")
			.unmarshal("locationDataFormat")
			.split(body())
				.setHeader("name",simple("${body.name}"))
				.to("direct:weather")
				.filter(predicate)
					.setHeader(Exchange.FILE_NAME,simple("${header.name}_weather.xml"))
					.to("file:/camelTest/output")
					.log("File ${header.CamelFileName} created")
				.end()
			.end();

		
		from("direct:weather")
			.routeId("weather-api")
			.setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body.id}&mode=xml"))
			.to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
			.convertBodyTo(String.class)
			;
		
	}

}
