package it.coderit.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.paho.client.mqttv3.MqttException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedeliveryExceptionRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		onException(MqttException.class).maximumRedeliveries(2).redeliveryDelay(1000)
				.log("Error processing message [${body}] ").handled(true);

		from("scheduler:start?initialDelay=1&delay=5000").setBody(simple("${date:now:yyyyMMdd HH-mm-ss}"))
				.log("Message [${body}] has been created.").to("paho:date?brokerUrl={{mqtt.broker}}")
				.log("Message [${body}]. Redelivery: [${header.CamelRedeliveryCounter}] has been sent.");

	}

}
