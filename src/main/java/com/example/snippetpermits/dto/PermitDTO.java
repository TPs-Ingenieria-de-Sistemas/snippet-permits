package com.example.snippetpermits.dto;

import com.example.snippetpermits.model.Permissions;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PermitDTO {
	@NotNull
	String userId;
	@NotNull
	Permissions permissions;
}
