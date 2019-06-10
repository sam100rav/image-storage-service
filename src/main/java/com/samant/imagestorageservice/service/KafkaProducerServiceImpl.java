package com.samant.imagestorageservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.samant.imagestorageservice.payload.ImageFileResponse;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService{
	
	@Autowired
	Environment env;
	
	@Autowired
	KafkaTemplate<String, ImageFileResponse> kafkaTemplate;

	@Override
	public void publish(ImageFileResponse message) {
		kafkaTemplate.send(env.getProperty("topic.name"), message);
		
	}

}
