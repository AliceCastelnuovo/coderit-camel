package it.coderit.camel.route;

import it.coderit.camel.model.City;
import it.coderit.camel.model.WeatherRequestParameters;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.processor.PredicateValidationException;
import org.apache.camel.tooling.model.Strings;

public class ReadFromFileRoute extends RouteBuilder {




    @Override
    public void configure() throws Exception {

        onException(PredicateValidationException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Invalid city found in CSV in file ${header.camelFileName}")
                .stop();

        Predicate idOrNameFilled = PredicateBuilder.not(
                PredicateBuilder.and(
                        method(Strings.class, "isNullOrEmpty(${body.name})"),
                        method(Strings.class, "isNullOrEmpty(${body.id})")
                )
        );



        BindyCsvDataFormat bindyCsvDataFormat = new BindyCsvDataFormat(City.class);



        from("file:/camelTest/csvFile?noop=true&include=.*.csv")
                .routeId("csvReader")
                .unmarshal(bindyCsvDataFormat)
                .split(body())
                .validate(idOrNameFilled)
                .bean(WeatherRequestParameters.class, "of")
                .log(LoggingLevel.DEBUG, "${body}")
                .to("seda:weatherService");

        from("file:/camelTest/jsonFile?noop=true&include=.*.gz")
                .routeId("jsonReader")
                .unmarshal().gzipDeflater()
                .unmarshal("locationDataFormat")
                .split(body())
                .validate(idOrNameFilled)
                .bean(WeatherRequestParameters.class, "of")
                .log(LoggingLevel.DEBUG, "${body}")
                .to("seda:weatherService");
    }

}
