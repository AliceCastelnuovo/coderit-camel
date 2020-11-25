package it.coderit.camel.utils;

import org.apache.camel.RecipientList;
import org.apache.camel.jsonpath.JsonPath;

public class LangBean {

    @RecipientList(ignoreInvalidEndpoints = true)
    public String[] route(String country) {

        if ("IT".equals(country)) {
            return new String[]{"direct:weather-en","direct:weather-it?block=false"};
        }else if("FR".equals(country)) {
            return new String[]{"direct:weather-en", "direct:weather-fr?block=false"};
        }else{
            return new String[]{"direct:weather-en"};
        }
    }
    
    public String recipients(@JsonPath(".country") String country) {
    	
        if ("IT".equals(country)) {
            return "direct:weather-en,direct:weather-it";
        }else if("FR".equals(country)) {
            return "direct:weather-en,direct:weather-fr";
        }else{
            return "direct:weather-en";
        }
    	
    	
    }
}
