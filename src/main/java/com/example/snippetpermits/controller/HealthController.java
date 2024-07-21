package com.example.snippetpermits.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
	private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

	@GetMapping
	public ResponseEntity<String> getHealth() {
		logger.atInfo().log("health: ok");
		return ResponseEntity.ok().build();
	}
	@GetMapping("fake-error")
	public ResponseEntity<String> fakeError() {
		logger.atError().log("health: ko");
		return ResponseEntity.internalServerError().build();
	}
}
