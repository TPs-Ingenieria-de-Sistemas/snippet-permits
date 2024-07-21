package com.example.snippetpermits.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "owner_id", "file_name"})})
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Permit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String userId;
	String ownerId;
	String fileName;
	Permissions rwx;

	/**
	 * creates a permit for a owner with all permission granted
	 */
	public Permit(String ownerId, String fileName) {
		userId = ownerId;
		this.ownerId = ownerId;
		this.fileName = fileName;
		rwx = Permissions.RWX;
	}

	public Permit(String userId, String ownerId, String fileName, Permissions permissions) {
		this.userId = userId;
		this.ownerId = ownerId;
		this.fileName = fileName;
		rwx = permissions;
	}

	public Permit extendPermissions(Permissions permissions) {
		return new Permit(id, userId, ownerId, fileName, rwx.add(permissions));
	}

	public Permit reducePermissions(Permissions permissions) {
		return new Permit(id, userId, ownerId, fileName, rwx.remove(permissions));
	}

	public boolean userHasPermission(Permissions permissions) {
		return rwx.hasPermission(permissions);
	}
}
