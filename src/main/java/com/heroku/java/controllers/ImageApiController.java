package com.heroku.java.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.heroku.java.models.Image;
import com.heroku.java.response.ResponseImage;
import com.heroku.java.response.ResponseMessage;
import com.heroku.java.services.ImageStorageService;

@CrossOrigin
@RestController
public class ImageApiController {

	@Autowired
	private ImageStorageService storageService;

	@PostMapping("/api/images")
	public ResponseEntity<ResponseMessage> uploadImage(@RequestParam("image") MultipartFile image) {
		String message = "";
		try {
			storageService.store(image);

			message = "Uploaded the image successfully: " + image.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		} catch (Exception e) {
			message = "Could not upload the image: " + image.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}

	@GetMapping("/api/images")
	public ResponseEntity<List<ResponseImage>> getListImages() {
		List<ResponseImage> images = storageService.getAllImages().map(image -> {
			String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/images/")
					.path(image.getId()).toUriString();

			return new ResponseImage(image.getName(), downloadUri, image.getType(), image.getData().length);
		}).collect(Collectors.toList());

		return ResponseEntity.status(HttpStatus.OK).body(images);
	}

	@GetMapping("/api/images/{id}")
	public ResponseEntity<Object> getImage(@PathVariable String id) {

		try {
			Image image = storageService.getImage(id);

			// Response type: byte[]
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"")
					.body(image.getData());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(e.getMessage()));
		}
	}

	@DeleteMapping("/api/images/{id}")
	public ResponseEntity<ResponseMessage> deletImageById(@PathVariable String id) {
		String message = "";
		message = storageService.deletImageById(id);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

	}
}