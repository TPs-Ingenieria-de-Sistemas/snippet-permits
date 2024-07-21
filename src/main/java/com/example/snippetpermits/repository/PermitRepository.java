package com.example.snippetpermits.repository;

import com.example.snippetpermits.model.Permit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitRepository extends JpaRepository<Permit, Long> {
	Permit findByFileNameAndOwnerIdAndUserId(String fileName, String ownerId, String userId);
}
