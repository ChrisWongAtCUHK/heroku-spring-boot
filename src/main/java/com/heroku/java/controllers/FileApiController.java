package com.heroku.java.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.heroku.java.services.storage.StorageService;

import java.util.stream.Collectors;

@RestController
public class FileApiController {

	private final StorageService storageService;

	@Autowired
	public FileApiController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/api/files")
	public Object listUploadedFilesArray() {
		Object files = storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileApiController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList());

		return files;
	}

	@GetMapping("/api/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/api/files")
	public String handleFileUpload(@RequestParam("file") MultipartFile file) {
		storageService.store(file);

		return "You successfully uploaded " + file.getOriginalFilename() + "!";
	}
}