package com.heroku.java.models;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {

	@Id
	@UuidGenerator
	private String id;

	private String name;

	private String type;

	@Lob
	private byte[] data;

	public Image() {
	}

	public Image(String name, String type, byte[] data) {
		this.name = name;
		this.type = type;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}