package com.samant.imagestorageservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.samant.imagestorageservice.payload.ImageFileResponse;

@Configuration
public class KafkaConfig {
	
	@Autowired
	Environment env;
	
	@Bean
	public ProducerFactory<String, ImageFileResponse> producerFactory() {
		Map<String, Object> configs = new HashMap<String, Object>();
		configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("bootstrap.servers.config"));
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		return new DefaultKafkaProducerFactory<String, ImageFileResponse>(configs);
	}
	
	@Bean
	public KafkaTemplate<String, ImageFileResponse> kafkaTemplate() {
		return new KafkaTemplate<String, ImageFileResponse>(producerFactory());
	}

}
