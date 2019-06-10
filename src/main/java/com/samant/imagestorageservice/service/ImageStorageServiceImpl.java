package com.samant.imagestorageservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.samant.imagestorageservice.exception.ImageStorgeServiceException;
import com.samant.imagestorageservice.model.ImageAlbum;
import com.samant.imagestorageservice.model.ImageFile;
import com.samant.imagestorageservice.repository.ImageAlbumRespository;
import com.samant.imagestorageservice.repository.ImageFileRepository;

@Service
@Transactional
public class ImageStorageServiceImpl implements ImageStorageService {

	@Autowired
	private ImageFileRepository imageRepository;

	@Autowired
	private ImageAlbumRespository albumRepository;

	@Override
	public ImageAlbum createAlbum(String albumName, String description) {
		ImageAlbum album = new ImageAlbum();
		album.setAlbumName(albumName);
		album.setDescription(description);
		return albumRepository.save(album);
	}

	@Override
	public ImageAlbum deleteAlbum(long albumId) {
		ImageAlbum album = albumRepository.findById(albumId).orElse(null);
		if (album == null)
			throw new ImageStorgeServiceException("Album not found with id " + albumId);

		imageRepository.deleteAll(imageRepository.findByImageAlbum(album));

		albumRepository.delete(album);
		return album;
	}

	@Override
	public ImageFile saveImage(long albumId, MultipartFile file) {
		ImageAlbum album = albumRepository.findById(albumId).orElse(null);
		if (album == null)
			throw new ImageStorgeServiceException("Album not found with id " + albumId);

		ImageFile image = new ImageFile();
		image.setImageAlbum(album);
		image.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
		image.setContentType(file.getContentType());
		image.setSize(file.getSize());
		try {
			image.setData(file.getBytes());
		} catch (IOException e) {
			throw new ImageStorgeServiceException("Could not store file " + image.getFileName() + ". Please try again later.");
		}
		return imageRepository.save(image);
	}

	@Override
	public ImageFile deleteImage(long imageId) {
		ImageFile image = imageRepository.findById(imageId).orElse(null);
		if (image == null)
			throw new ImageStorgeServiceException("Image not found with id " + imageId);

		imageRepository.deleteById(imageId);
		return image;
	}

	@Override
	public ImageFile findImageById(long imageId) {
		return imageRepository.findById(imageId)
				.orElseThrow(() -> new ImageStorgeServiceException("Image not found with id " + imageId));
	}

	@Override
	public List<ImageFile> findAllImagesInOneAlbum(long albumId) {
		ImageAlbum album = albumRepository.findById(albumId).orElse(null);
		if (album == null)
			throw new ImageStorgeServiceException("Album not found with id " + albumId);

		List<ImageFile> imageFileList = new ArrayList<ImageFile>();
		imageRepository.findByImageAlbum(album).forEach(imageFileList::add);
		return imageFileList;
	}

}
