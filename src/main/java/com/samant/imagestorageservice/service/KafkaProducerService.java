package com.samant.imagestorageservice.service;

import com.samant.imagestorageservice.payload.ImageFileResponse;

public interface KafkaProducerService {
	
	void publish(ImageFileResponse message);
}
