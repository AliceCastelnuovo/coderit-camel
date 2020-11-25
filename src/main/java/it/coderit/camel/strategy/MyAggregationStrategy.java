package it.coderit.camel.strategy;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class MyAggregationStrategy implements AggregationStrategy {

    /**
     * Aggregates the messages.
     *
     * @param oldExchange  the existing aggregated message. Is <tt>null</tt> the
     *                     very first time as there are no existing message.
     * @param newExchange  the incoming message. This is never <tt>null</tt>.
     * @return the aggregated message.
     */
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if (oldExchange == null) {
            return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class).trim();
        String newBody = newExchange.getIn().getBody(String.class).trim();

        String body = oldBody + newBody;

        oldExchange.getIn().setBody(body);
        return oldExchange;
    }
    
}
