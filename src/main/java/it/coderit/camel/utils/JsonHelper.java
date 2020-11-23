package it.coderit.camel.utils;

import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.jsonpath.JsonPath;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

	private final ObjectMapper mapper = new ObjectMapper();

	@Handler
	public Object extract(String body, String key) throws Exception {
		
		Map<String,Object> object = mapper.readValue(body,new TypeReference<Map<String,Object>>(){});
		return object.get(key);
	
	}

	public String extract(@JsonPath("$.name") String name,@JsonPath("$.country") String country,@JsonPath("$.id") String id){

		return name + "," + country + "," + id;
	};

}
