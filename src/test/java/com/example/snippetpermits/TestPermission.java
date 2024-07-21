package com.example.snippetpermits;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.snippetpermits.model.Permissions;
import org.junit.jupiter.api.Test;

public class TestPermission {
	@Test
	public void test001_fromRExtendToRW() {
		Permissions result = Permissions.R.add(Permissions.W);
		assertEquals(Permissions.RW, result);
	}

	@Test
	public void test002_fromRExtendToRWX() {
		Permissions result = Permissions.R.add(Permissions.WX);
		assertEquals(Permissions.RWX, result);
	}

	@Test
	public void test003_fromRWReduceToR() {
		Permissions result = Permissions.RW.remove(Permissions.W);
		assertEquals(Permissions.R, result);
	}

	@Test
	public void test004_fromRWXReduceToR() {
		Permissions result = Permissions.RWX.remove(Permissions.WX);
		assertEquals(Permissions.R, result);
	}
}
