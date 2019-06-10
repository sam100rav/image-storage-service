package com.samant.imagestorageservice.repository;

import org.springframework.stereotype.Repository;

import com.samant.imagestorageservice.model.ImageAlbum;
import com.samant.imagestorageservice.model.ImageFile;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface ImageFileRepository extends CrudRepository<ImageFile, Long>{
	
	Iterable<ImageFile> findByImageAlbum(ImageAlbum album);

}
