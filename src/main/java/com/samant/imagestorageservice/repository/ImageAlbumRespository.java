package com.samant.imagestorageservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.samant.imagestorageservice.model.ImageAlbum;

public interface ImageAlbumRespository extends CrudRepository<ImageAlbum, Long> {

}
