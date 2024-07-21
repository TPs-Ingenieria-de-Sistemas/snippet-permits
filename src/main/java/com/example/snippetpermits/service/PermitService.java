package com.example.snippetpermits.service;

import com.example.snippetpermits.exeption.ConflictException;
import com.example.snippetpermits.exeption.EntityNotFoundException;
import com.example.snippetpermits.exeption.ForbiddenException;
import com.example.snippetpermits.model.Permissions;
import com.example.snippetpermits.model.Permit;
import com.example.snippetpermits.repository.PermitRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class PermitService {
	private final PermitRepository repository;
	public PermitService(PermitRepository repository) {
		this.repository = repository;
	}

	public Permit addOwnerPermits(String ownerId, String fileName) {
		try {
			Permit permit = new Permit(ownerId, fileName);
			return repository.save(permit);
		} catch (Exception exc) {
			if (exc instanceof org.springframework.dao.DataIntegrityViolationException) {
				throw new ConflictException("the user already is owner of a file with that name");
			}
			throw exc;
		}
	}

	public Permit sharePermitsForSnippet(String ownerId, String fileName, String userId, Permissions permissions) {
		if (theUserIsTheOwner(userId, ownerId))
			throw new ForbiddenException("Owner can't modify it's own permissions");
		if (!isOwner(ownerId, fileName))
			throw new ForbiddenException("the user is not the owner of the file");

		Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, userId);
		if (permit == null)
			return repository.save(new Permit(userId, ownerId, fileName, permissions));

		permit = permit.extendPermissions(permissions);
		return repository.save(permit);
	}

	public Permit removePermitsForSnippet(String ownerId, String fileName, String userId, Permissions permissions) {
		if (theUserIsTheOwner(userId, ownerId))
			throw new ForbiddenException("Owner can't modify it's own permissions");
		if (!isOwner(ownerId, fileName))
			throw new ForbiddenException("the user is not the owner of the file");

		Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, userId);
		if (permit == null)
			throw new EntityNotFoundException("no permission found for the user");

		if (permissions == Permissions.RWX) {
			repository.delete(permit);
			return null;
		} else {
			permit = permit.reducePermissions(permissions);
			return repository.save(permit);
		}
	}

	public boolean userHasPermission(String userId, String ownerId, String fileName, Permissions permissions) {
		if (theUserIsTheOwner(userId, ownerId))
			return true;

		Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, userId);
		if (permit == null)
			throw new EntityNotFoundException("no permission found for the user");

		return permit.userHasPermission(permissions);
	}

	private boolean isOwner(String ownerId, String fileName) {
		Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, ownerId);
		return permit != null;
	}

	private boolean theUserIsTheOwner(String userId, String ownerId) {
		return Objects.equals(userId, ownerId);
	}
}
