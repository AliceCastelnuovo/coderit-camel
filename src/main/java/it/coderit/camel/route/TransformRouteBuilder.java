package it.coderit.camel.route;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import it.coderit.camel.model.Location;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TransformRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("file:/camelTest/cities?noop=true")
                .unmarshal().json(JsonLibrary.Jackson,Location.class)
                .setHeader("city",simple("${body.name}"))
                .setHeader("country",simple("${body.country}"))
                .setHeader("id",simple("${body.id}"))
                .setHeader(Exchange.HTTP_QUERY,simple("appid={{weather.api.key}}&id=${header.id}"))
                .to("https:{{weather.api.url}}/{{weather.api.path}}?httpMethod=GET")
                .setHeader(Exchange.FILE_NAME,simple("${header.city}_weather.gz"))
                .marshal().gzipDeflater()
                .toD("file:/camelTest/output/${header.country}")
                .log("File [${header.CamelFileName}] created.");
    }
}
