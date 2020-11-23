package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import it.coderit.camel.model.Location;


public class DirectRouteBuilder extends RouteBuilder{

	public static final String ROUTE_ID = "ask-weather";
	
	@Override
	public void configure() throws Exception {

		from("file:{{file.fromPath}}?noop=true")
				.routeId("read-file")
			.unmarshal().json(JsonLibrary.Jackson,Location.class)
		.to("direct:weather");



		from("direct:weather")
			.routeId(ROUTE_ID)
			.setHeader("city",simple("${body.name}"))
			.setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body.id}"))
			.to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
			.setHeader(Exchange.FILE_NAME,simple("${header.city}_weather.json"))
		.to("file:{{file.toPath}}")
		.log("File [${header.CamelFileName}] created.");
		
	}

}
