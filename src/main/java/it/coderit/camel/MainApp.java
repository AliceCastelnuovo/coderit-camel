package it.coderit.camel;

import org.apache.camel.main.Main;

import it.coderit.camel.route.SedaRouteBuilder;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new SedaRouteBuilder());
        main.run(args);
    }

}
