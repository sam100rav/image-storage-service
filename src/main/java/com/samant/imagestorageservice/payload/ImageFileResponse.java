package com.samant.imagestorageservice.payload;

public class ImageFileResponse {

	private long id;

	private String fileName;

	private String imageDownloadUri;

	private String contentType;

	private long size;

	private long albumId;

	public ImageFileResponse(long id, String fileName, String imageDownloadUri, String contentType, long size,
			long albumId) {
		this.id = id;
		this.fileName = fileName;
		this.imageDownloadUri = imageDownloadUri;
		this.contentType = contentType;
		this.size = size;
		this.albumId = albumId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getImageDownloadUri() {
		return imageDownloadUri;
	}

	public void setImageDownloadUri(String imageDownloadUri) {
		this.imageDownloadUri = imageDownloadUri;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

}
