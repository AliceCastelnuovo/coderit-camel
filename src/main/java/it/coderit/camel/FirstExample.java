package it.coderit.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;


public class FirstExample {

	public static void main(String[] args) throws Exception {
		
	CamelContext context = new DefaultCamelContext();
	
	
	context.addRoutes(new RouteBuilder() {
			
			public void configure() {
				
				from("file:/camelTest?noop=true&include=.*.gz")
					.unmarshal().gzipDeflater()
					.setHeader(Exchange.FILE_NAME,simple("${file:name.noext.single}"))
				.to("file:/camelTest/output");

				
			}
		});

		context.start();
		
		Thread.sleep(5000);
		
		context.stop();
		
	}
}
