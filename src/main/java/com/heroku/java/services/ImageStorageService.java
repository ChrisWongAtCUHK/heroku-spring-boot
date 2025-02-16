package com.heroku.java.services;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.models.Image;
import com.heroku.java.repositories.ImageRepository;

@Service
public class ImageStorageService {

	@Autowired
	private ImageRepository imageRepository;

	public Image store(MultipartFile file) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Image image = new Image(fileName, file.getContentType(), file.getBytes());

		return imageRepository.save(image);
	}

	public Image getImage(String id) {
		return imageRepository.findById(id).get();
	}

	public Stream<Image> getAllImages() {
		return imageRepository.findAll().stream();
	}

	public String deletImageById(String id) {
		if (imageRepository.existsById(id)) {
			imageRepository.deleteById(id);
			return "Image has been successfully deleted";
		}
		return "Image doesn't exist";
	}
}