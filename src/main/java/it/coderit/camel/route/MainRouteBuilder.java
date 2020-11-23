package it.coderit.camel.route;

import org.apache.camel.builder.RouteBuilder;

public class MainRouteBuilder extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("file:/camelTest/cities?noop=true")
		.setHeader("camelFileName",simple("${date:now:{{dateFormat}}}-${header.camelFileName}.json"))
		.log("${header.camelFileName}")
		.to("file:/camelTest/output");
		
	}

}
