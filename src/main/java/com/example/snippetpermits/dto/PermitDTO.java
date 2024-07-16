package com.example.snippetpermits.dto;

import com.example.snippetpermits.model.Permissions;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PermitDTO {
    @NotNull
    Long userId;
    @NotNull
    Permissions permissions;
}
