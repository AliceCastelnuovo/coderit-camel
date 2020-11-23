package it.coderit.camel.route;

import org.apache.camel.builder.RouteBuilder;

public class BeanRouteBuilder extends RouteBuilder {


	@Override
	public void configure() throws Exception {
		
		from("file:{{file.fromPath}}?noop=true")
		.bean("jsonHelper","extract(${body},'name')")
				.log("${body}")
		.to("file:{{file.toPath}}");
		
	}
	
	

}
