package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class FileDownloadControllerUtilsTest {

	@Test
	public void fileResponseEntity() {
		ByteArrayResource resource = new ByteArrayResource("somebytes".getBytes());

		FileEntryResource fileEntry = new FileEntryResource();
		fileEntry.setMediaType(MediaType.IMAGE_JPEG_VALUE);
		fileEntry.setName("name");
		
		ResponseEntity<ByteArrayResource> result = FileDownloadControllerUtils.getFileResponseEntity(resource, fileEntry);
		
		assertEquals("inline; filename=\"name\"", result.getHeaders().get("Content-Disposition").get(0));
		assertEquals("9", result.getHeaders().get("Content-Length").get(0));
		assertEquals(MediaType.IMAGE_JPEG_VALUE, result.getHeaders().get("Content-Type").get(0));
		assertArrayEquals("somebytes".getBytes(), result.getBody().getByteArray());
	}
	
	@Test
	public void fileResponseEntityMissingName() {
		ByteArrayResource resource = new ByteArrayResource("somebytes".getBytes());

		FileEntryResource fileEntry = new FileEntryResource();
		fileEntry.setMediaType(MediaType.IMAGE_JPEG_VALUE);
		
		ResponseEntity<ByteArrayResource> result = FileDownloadControllerUtils.getFileResponseEntity(resource, fileEntry);
		
		assertNull(result.getHeaders().get("Content-Disposition"));
		assertEquals("9", result.getHeaders().get("Content-Length").get(0));
		assertEquals(MediaType.IMAGE_JPEG_VALUE, result.getHeaders().get("Content-Type").get(0));
		assertArrayEquals("somebytes".getBytes(), result.getBody().getByteArray());
	}
}
