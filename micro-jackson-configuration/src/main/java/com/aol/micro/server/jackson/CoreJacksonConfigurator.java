package com.aol.micro.server.jackson;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aol.micro.server.PluginLoader;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class CoreJacksonConfigurator implements JacksonMapperConfigurator {
	private final Optional<Include> inc;
	@Autowired
	public CoreJacksonConfigurator(@Value("${jackson.seriliazation:NON_NULL}")Include inc){
		this.inc = Optional.ofNullable(inc);
	}
	public void accept(ObjectMapper mapper) {
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		// configure as necessary
		mapper.registerModule(module);
		inc.map(include->mapper.setSerializationInclusion(include));
		PluginLoader.INSTANCE.plugins.get().stream()
			.filter(m -> m.jacksonModules()!=null)
			.flatMap(m -> m.jacksonModules().stream())
			.forEach(m -> mapper.registerModule(m));
			
		mapper.registerModule(new Jdk8Module());
	}

}