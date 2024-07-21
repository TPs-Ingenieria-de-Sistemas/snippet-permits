package com.example.snippetpermits.controller;

import com.example.snippetpermits.dto.PermitDTO;
import com.example.snippetpermits.model.Permissions;
import com.example.snippetpermits.model.Permit;
import com.example.snippetpermits.service.PermitService;
import com.example.snippetpermits.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PermitController {
	private final PermitService permitService;
	private final UserService userService;

	public PermitController(PermitService permitService, UserService userService) {
		this.permitService = permitService;
		this.userService = userService;
	}

	@PostMapping("/manage/as-owner/{fileName}")
	public ResponseEntity<Permit> postOwnerPermit(@PathVariable String fileName) {
		String ownerId = userService.getUserId();
		Permit permit = permitService.addOwnerPermits(ownerId, fileName);
		return ResponseEntity.status(HttpStatus.CREATED).body(permit);
	}

	@PostMapping("/manage/{fileName}")
	public ResponseEntity<Permit> postPermit(@RequestBody @Valid PermitDTO permitDTO, @PathVariable String fileName) {
		String ownerId = userService.getUserId();
		Permit permit = permitService.sharePermitsForSnippet(ownerId, fileName, permitDTO.getUserId(),
				permitDTO.getPermissions());
		return ResponseEntity.status(HttpStatus.OK).body(permit);
	}

	@DeleteMapping("/manage/{fileName}")
	public ResponseEntity<Permit> removePermits(@RequestBody @Valid PermitDTO permitDTO,
			@PathVariable String fileName) {
		String ownerId = userService.getUserId();
		Permit permit = permitService.removePermitsForSnippet(ownerId, fileName, permitDTO.getUserId(),
				permitDTO.getPermissions());
		return ResponseEntity.status(HttpStatus.OK).body(permit);
	}

	@GetMapping("/{ownerId}/{fileName}")
	public ResponseEntity<Boolean> hasPermissions(@PathVariable String ownerId, @PathVariable String fileName,
			@RequestParam Permissions permission) {
		String userId = userService.getUserId();
		Boolean hasPermission = permitService.userHasPermission(userId, ownerId, fileName, permission);
		return ResponseEntity.status(HttpStatus.OK).body(hasPermission);
	}
}
