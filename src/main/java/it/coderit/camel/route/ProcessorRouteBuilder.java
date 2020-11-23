package it.coderit.camel.route;

import org.apache.camel.builder.RouteBuilder;

import it.coderit.camel.processor.Extractor;

public class ProcessorRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("file:/camelTest/cities?noop=true")
		.process(new Extractor("name"))
		.log("${body}")
		.to("file:/camelTest/output");
		
	}
	
	

}
