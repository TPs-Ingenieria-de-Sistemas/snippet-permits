package com.example.snippetpermits.model;

import lombok.Getter;

@Getter
public enum Permissions {
	X(0b001), // 001
	W(0b010), // 010
	WX(0b011), // 011
	R(0b100), // 100
	RX(0b101), // 101
	RW(0b110), // 110
	RWX(0b111); // 111

	private final int value;

	Permissions(int value) {
		this.value = value;
	}

	public static Permissions fromValue(int value) {
		for (Permissions p : Permissions.values()) {
			if (p.getValue() == value) {
				return p;
			}
		}
		throw new IllegalArgumentException("Invalid permission value: " + value);
	}

	public Permissions add(Permissions permissions) {
		int newPermissions = this.getValue() | permissions.getValue();
		return Permissions.fromValue(newPermissions);
	}

	public Permissions remove(Permissions permissions) {
		int newPermissions = this.getValue() & ~permissions.getValue();
		return Permissions.fromValue(newPermissions);
	}

	public boolean hasPermission(Permissions permissions) {
		return (this.getValue() & permissions.getValue()) == permissions.getValue();
	}
}
