package com.example.snippetpermits.controller;

import com.example.snippetpermits.dto.PermitDTO;
import com.example.snippetpermits.model.Permissions;
import com.example.snippetpermits.model.Permit;
import com.example.snippetpermits.service.PermitService;
import com.example.snippetpermits.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
			@RequestParam Integer permission) throws BadRequestException {
		String userId = userService.getUserId();
		try {
			Permissions actualPermission = Permissions.fromValue(permission);
			Boolean hasPermission = permitService.userHasPermission(userId, ownerId, fileName, actualPermission);
			return ResponseEntity.status(HttpStatus.OK).body(hasPermission);
		}catch (IllegalArgumentException e) {
			throw new BadRequestException("expects a value between 0 and 10, representing RWX");
		}

	}

	@GetMapping("/get-with-r-access")
	public ResponseEntity<Page<?>> getAllReadableSnippets(Pageable pageable) {
		String userId = userService.getUserId();
		Page<?> page = permitService.getAllReadableSnippets(userId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(page);
	}
}
