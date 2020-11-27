package it.coderit.camel.route;

import it.coderit.camel.model.Location;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SedaThrottleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:weatherService")
                .routeId("throttleWeather")
                .throttle(60).timePeriodMillis(60000)
                .setHeader(Exchange.HTTP_QUERY, simple("${body.queryParameter}&appid={{weather.api.key}}"))
                .setHeader(Exchange.HTTP_PATH, simple("{{weather.api.path}}"))
                .to("https:{{weather.api.url}}")
                .log("${date:now}")
                .to("seda:mongoService");
    }
}
