package it.coderit.camel.route;

import com.mongodb.client.model.Filters;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;

public class MongoRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:mongoService")
            .convertBodyTo(String.class)
                .to("mongodb:connectionBean?database={{mongoDBName}}&collection={{mongoDBCollection}}&operation=insert");



        from("scheduler:startFileRoute?initialDelay=20000&repeatCount=1")
                .log("MongoRouteBuilder STARTED")
                .setHeader(MongoDbConstants.CRITERIA, ExpressionBuilder.constantExpression(Filters.eq("name", "Roma")))
                .to("mongodb:connectionBean?database={{mongoDBName}}&collection={{mongoDBCollection}}&operation=findAll&outputType=MongoIterable")
                .split(body())
                .log(LoggingLevel.INFO, "${body}")
                .to("mock:endpoint");
    }
}
