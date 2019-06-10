package com.samant.imagestorageservice.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.samant.imagestorageservice.model.ImageAlbum;
import com.samant.imagestorageservice.model.ImageFile;

public interface ImageStorageService {

	ImageAlbum createAlbum(String albumName, String description);

	ImageAlbum deleteAlbum(long albumId);

	ImageFile saveImage(long albumId, MultipartFile file);

	ImageFile deleteImage(long imageId);

	ImageFile findImageById(long imageId);

	List<ImageFile> findAllImagesInOneAlbum(long albumId);
}
