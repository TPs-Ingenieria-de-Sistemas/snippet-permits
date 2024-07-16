package com.example.snippetpermits.service;

import com.example.snippetpermits.exeption.ConflictException;
import com.example.snippetpermits.exeption.EntityNotFoundException;
import com.example.snippetpermits.exeption.ForbiddenException;
import com.example.snippetpermits.model.Permissions;
import com.example.snippetpermits.model.Permit;
import com.example.snippetpermits.repository.PermitRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PermitService {
    private final PermitRepository repository;
    public PermitService(PermitRepository repository) {
        this.repository = repository;
    }

    public Permit addOwnerPermits(Long ownerId, String fileName) {
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

    public Permit sharePermitsForSnippet(Long ownerId, String fileName, Long userId, Permissions permissions) {
        if (theUserIsTheOwner(userId, ownerId)) throw new ForbiddenException("Owner can't modify it's own permissions");
        if (!isOwner(ownerId, fileName)) throw new ForbiddenException("the user is not the owner of the file");

        Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, userId);
        if (permit == null) return repository.save(new Permit(userId, ownerId, fileName, permissions));

        permit = permit.extendPermissions(permissions);
        return repository.save(permit);
    }

    public Permit removePermitsForSnippet(Long ownerId, String fileName, Long userId, Permissions permissions) {
        if (theUserIsTheOwner(userId, ownerId)) throw new ForbiddenException("Owner can't modify it's own permissions");
        if (!isOwner(ownerId, fileName)) throw new ForbiddenException("the user is not the owner of the file");

        Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, userId);
        if (permit == null) throw new EntityNotFoundException("no permission found for the user");

        if (permissions == Permissions.RWX) {
            repository.delete(permit);
            return null;
        }
        else {
            permit = permit.reducePermissions(permissions);
            return repository.save(permit);
        }
    }

    public boolean userHasPermission(Long userId, Long ownerId, String fileName, Permissions permissions) {
        if (theUserIsTheOwner(userId, ownerId)) return true;

        Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, userId);
        if (permit == null) throw new EntityNotFoundException("no permission found for the user");

        return permit.userHasPermission(permissions);
    }

    private boolean isOwner(Long ownerId, String fileName) {
        Permit permit = repository.findByFileNameAndOwnerIdAndUserId(fileName, ownerId, ownerId);
        return permit != null;
    }

    private boolean theUserIsTheOwner(Long userId, Long ownerId) {
        return Objects.equals(userId, ownerId);
    }
}
