package it.coderit.camel;

import org.apache.camel.main.Main;

import it.coderit.camel.route.MongoRouteBuilder;
import it.coderit.camel.route.ReadFromFileRoute;
import it.coderit.camel.route.SedaThrottleRoute;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
    	
        Main main = new Main();
        main.configure().addConfiguration(new CamelConfig());
        main.configure().addConfiguration(new CamelConfig());
        main.configure().addRoutesBuilder(new ReadFromFileRoute());
        main.configure().addRoutesBuilder(new SedaThrottleRoute());
        main.configure().addRoutesBuilder(new MongoRouteBuilder());
        main.run(args);
    }

}

