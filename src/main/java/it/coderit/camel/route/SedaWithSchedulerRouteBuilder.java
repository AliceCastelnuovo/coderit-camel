package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;

import org.apache.camel.model.dataformat.JsonLibrary;

import it.coderit.camel.model.Location;


public class SedaWithSchedulerRouteBuilder extends RouteBuilder{

	
	@Override
	public void configure() throws Exception {

        from("file:{{file.fromPath}}?noop=true")
        .routeId("read-file")
        .unmarshal().json(JsonLibrary.Jackson,Location.class)
        .log("Send city to blockingQueue...")
        .to("seda:weather");

		from("seda:weather")
		        .log("Read city from blockingQueue ${body.name}")
		        .autoStartup(Boolean.FALSE)
		        .routeId("ask-weather")
		        .setHeader("city",simple("${body.name}"))
		        .setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${body.id}"))
		        .to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
		        .setHeader(Exchange.FILE_NAME,simple("${header.city}_weather.json"))
		        .to("file:{{file.toPath}}")
		        .log(LoggingLevel.DEBUG,"File [${header.CamelFileName}] created.");



		from("scheduler:startFileRoute?initialDelay=10000&repeatCount=1")
		        .routeId("controlbus-start-route")
		        .to("controlbus:route?routeId=ask-weather&action=start");
		
	}

}
