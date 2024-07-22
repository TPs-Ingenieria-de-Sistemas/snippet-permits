package com.example.snippetpermits.repository;

import com.example.snippetpermits.model.Permit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermitRepository extends JpaRepository<Permit, Long> {
	Permit findByFileNameAndOwnerIdAndUserId(String fileName, String ownerId, String userId);
	@Query(value = "SELECT p FROM Permit p WHERE (p.userId = :userId AND p.rwx >= 4) OR p.ownerId = :userId")
	Page<Permit> findByUserIdAndRPermissionsOrOwnerId(@Param("userId") String userId, Pageable pageable);
}
