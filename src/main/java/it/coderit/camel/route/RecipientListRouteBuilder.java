package it.coderit.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class RecipientListRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("file:/camelTest/cities?noop=true")
                .setHeader("name",jsonpath("$.name"))
                .setHeader("id",jsonpath("$.id"))
                .setHeader("country",jsonpath("$.country"))
                .setHeader("recipients",method("langBean","recipients"))   
                .recipientList(header("recipients"))
      //          .bean("langBean","route(${header.country})")
                ;

        from("direct:weather-it")
            .setHeader("lang",simple("it"))
                .to("direct:weather");

        from("direct:weather-en")
                .setHeader("lang",simple("en"))
                    .to("direct:weather");

        from("direct:weather-fr")
                .setHeader("lang",simple("fr"))
                .to("direct:weather");

         from("direct:weather")
                .routeId("weather-api")
                .setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${header.id}&lang=${header.lang}"))
                .to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
                .log(LoggingLevel.DEBUG, "${body}")
                .setHeader(Exchange.FILE_NAME,simple("${header.country}_${header.name}_weather.json"))
                .toD("file:/camelTest/output/${header.lang}")
                .log("File ${header.CamelFileName} created");
    }
}
