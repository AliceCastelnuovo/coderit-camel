package it.coderit.camel;

import org.apache.camel.BindToRegistry;
import org.apache.camel.component.seda.SedaComponent;

import it.coderit.camel.processor.Extractor;

public class CamelConfig {

	@BindToRegistry
	public Extractor nameExtractor() {
		return new Extractor("name");
	}
	
	@BindToRegistry(value = "seda")
	public SedaComponent sedaComponent() {

		SedaComponent sedaComponent = new SedaComponent();
		sedaComponent.setDefaultBlockWhenFull(Boolean.TRUE);
		sedaComponent.setConcurrentConsumers(10);
		sedaComponent.setQueueSize(100);
		return sedaComponent;
	};

}
