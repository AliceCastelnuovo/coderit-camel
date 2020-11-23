package it.coderit.camel.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Extractor implements Processor{

	private final String field;
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		String body = exchange.getIn().getBody(String.class);
		Map<String,Object> object = mapper.readValue(body,new TypeReference<Map<String,Object>>(){});
		exchange.getIn().setBody(object.get(field));
		
	}

}
