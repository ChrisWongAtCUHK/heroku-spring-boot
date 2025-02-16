package com.heroku.java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heroku.java.models.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
}