package com.samant.imagestorageservice.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.samant.imagestorageservice.exception.ImageStorgeServiceException;
import com.samant.imagestorageservice.model.ImageAlbum;
import com.samant.imagestorageservice.model.ImageFile;
import com.samant.imagestorageservice.payload.ImageFileResponse;
import com.samant.imagestorageservice.service.ImageStorageService;
import com.samant.imagestorageservice.service.KafkaProducerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "Image Storage Service")
public class ImageStorageResource {

	@Autowired
	private ImageStorageService imageService;

	@Autowired
	private KafkaProducerService kafkaService;

	private static final Logger logger = LoggerFactory.getLogger(ImageStorageResource.class);

	@PostMapping("/album")
	@ApiOperation(value = "Make a POST call to create an album", produces = "application/json", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The POST call is successfull"),
			@ApiResponse(code = 500, message = "The POST call is failed"),
			@ApiResponse(code = 404, message = "The API could not be found") })
	public ImageAlbum createAlbum(
			@ApiParam(name = "albumName", value = "Name of the album", required = true) @RequestParam String albumName,
			@ApiParam(name = "description", value = "Description of the allbum", required = true) @RequestParam String description) {
		return imageService.createAlbum(albumName, description);
	}

	@RequestMapping(value = "/album/{albumId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Make a DELETE call to delete an album", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The DELETE call is successfull"),
			@ApiResponse(code = 500, message = "The DELETE call is failed"),
			@ApiResponse(code = 404, message = "The API or album to be deleted could not be found") })
	public ImageAlbum deleteAlbum(
			@ApiParam(name = "albumId", value = "ID of the album to be deleted", required = true) @PathVariable long albumId) {
		return imageService.deleteAlbum(albumId);
	}

	@PostMapping("/album/{albumId}/images")
	@ApiOperation(value = "Make a POST call to upload/create an image", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The POST call is successfull"),
			@ApiResponse(code = 500, message = "Image storage exception or multipart exception"),
			@ApiResponse(code = 404, message = "The API or the album to upload image to could not be found") })
	public ImageFileResponse uploadImage(
			@ApiParam(name = "albumId", value = "ID of the album to upload image to", required = true) @PathVariable long albumId,
			@ApiParam(name = "image", value = "Select the image file to upload", required = true) @RequestParam MultipartFile image) {
		if (!image.getContentType().startsWith("image/"))
			throw new ImageStorgeServiceException("Uploaded file is not an image");

		ImageFile imageFile = imageService.saveImage(albumId, image);

		String imageDownloadUri = MvcUriComponentsBuilder
				.fromMethodName(ImageStorageResource.class, "downloadImage", String.valueOf(imageFile.getImageId()))
				.build().toString();

		ImageFileResponse response = new ImageFileResponse(imageFile.getImageId(), imageFile.getFileName(),
				imageDownloadUri, imageFile.getContentType(), imageFile.getSize(),
				imageFile.getImageAlbum().getAlbumId());

		try {
			kafkaService.publish(response);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		return response;
	}

	@RequestMapping(value = "/images/{imageId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Make a DELETE call to delete an image", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The DELETE call is successfull"),
			@ApiResponse(code = 500, message = "The DELETE call is failed"),
			@ApiResponse(code = 404, message = "The API or image to be deleted could not be found") })
	public ImageFileResponse deleteImage(
			@ApiParam(name = "imageId", value = "ID of the image to be deleted", required = true) @PathVariable long imageId) {
		ImageFile imageFile = imageService.deleteImage(imageId);

		ImageFileResponse response = new ImageFileResponse(imageFile.getImageId(), imageFile.getFileName(), null,
				imageFile.getContentType(), imageFile.getSize(), imageFile.getImageAlbum().getAlbumId());

		try {
			kafkaService.publish(response);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		return response;
	}

	@GetMapping("/album/{albumId}/images/")
	@ApiOperation(value = "Make a GET call to fetch all images in an album", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The GET call is successfull"),
			@ApiResponse(code = 500, message = "The GET call is failed"),
			@ApiResponse(code = 404, message = "The GET call or the album of images to be fetched could not be found") })
	public List<ImageFileResponse> getAllImageInOneAlbum(
			@ApiParam(name = "albumId", value = "ID of the album of the images to be fetched", required = true) @PathVariable long albumId) {
		List<ImageFile> imageList = imageService.findAllImagesInOneAlbum(albumId);

		return imageList.stream()
				.map(image -> new ImageFileResponse(image.getImageId(), image.getFileName(),
						MvcUriComponentsBuilder.fromMethodName(ImageStorageResource.class, "downloadImage",
								String.valueOf(image.getImageId())).build().toString(),
						image.getContentType(), image.getSize(), image.getImageAlbum().getAlbumId()))
				.collect(Collectors.toList());

	}

	@GetMapping("/images/{imageId}")
	@ApiOperation(value = "Make a GET call to download an image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The GET call is successfull"),
			@ApiResponse(code = 500, message = "The GET call is failed"),
			@ApiResponse(code = 404, message = "The GET call or the image to be downloaded could not be found") })
	public ResponseEntity<Resource> downloadImage(
			@ApiParam(name = "imageId", value = "ID of the image to be downloaded", required = true) @PathVariable long imageId) {
		ImageFile image = imageService.findImageById(imageId);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
				.body(new ByteArrayResource(image.getData()));
	}

}
