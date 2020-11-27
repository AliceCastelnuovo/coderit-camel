package it.coderit.camel.model;

import lombok.Data;
import org.apache.camel.tooling.model.Strings;

@Data
public class WeatherRequestParameters {
    private final String queryParameter;


    public static WeatherRequestParameters byName(String name) {
        return new WeatherRequestParameters("q=" + name);
    }

    public static WeatherRequestParameters byId(String name) {
        return new WeatherRequestParameters("id=" + name);
    }

    public static WeatherRequestParameters of(City city) {
        if(!Strings.isNullOrEmpty(city.getId())) {
            return byId(city.getId());
        }
        return byName(city.getName());
    }

    public static WeatherRequestParameters of(Location location) {
        if(!Strings.isNullOrEmpty(location.getId())) {
            return byId(location.getId());
        }
        return byName(location.getName());
    }

}
