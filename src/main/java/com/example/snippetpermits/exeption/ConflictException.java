package com.example.snippetpermits.exeption;

public class ConflictException extends RuntimeException {
	public ConflictException(String message) {
		super(message);
	}
}
