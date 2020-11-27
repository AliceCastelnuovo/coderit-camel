package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.processor.PredicateValidationException;
import org.apache.camel.util.StringHelper;

import it.coderit.camel.model.Location;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandledExceptionRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		onException(PredicateValidationException.class)
		.handled(true)
		.process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {

				Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
				log.error("Error processing file [{}]: {}", exchange.getIn().getHeader(Exchange.FILE_NAME),
						caused.getLocalizedMessage());
			}
		})
		.stop();

		Predicate idIsNotBlank = method(StringHelper.class, "isNotEmpty(${body.id})");

		from("file:/camelTest/cities?noop=true&delay=5000&moveFailed=/camelTest/error/${file:name.noext}.${file:ext}")
				.unmarshal()
				.json(JsonLibrary.Jackson, Location.class)
				.setHeader("name", simple("${body.name}"))
				.to("direct:weather")
				.setHeader(Exchange.FILE_NAME, simple("${header.name}_weather.json"))
				.to("file:/camelTest/output").log("File ${header.CamelFileName} created");

		from("direct:weather").routeId("weather-api")
				.validate(idIsNotBlank)
				.setHeader(Exchange.HTTP_QUERY, simple("appid={{weather.api.key}}&id=${body.id}"))
				.to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
				.log(LoggingLevel.DEBUG, "${body}");

	}

}
