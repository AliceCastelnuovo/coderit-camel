package it.coderit.camel.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import lombok.Data;

@Data
public class Location {

	private String id;

	private String name;

	private String state;

	private String country;

	@JsonProperty(value = "coord")
	private Map<String,String> coordinates;

}
