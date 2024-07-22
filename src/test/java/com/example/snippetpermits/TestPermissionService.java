package com.example.snippetpermits;

import static org.junit.jupiter.api.Assertions.*;

import com.example.snippetpermits.exeption.ConflictException;
import com.example.snippetpermits.exeption.ForbiddenException;
import com.example.snippetpermits.model.Permissions;
import com.example.snippetpermits.model.Permit;
import com.example.snippetpermits.service.PermitService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestPermissionService {

	@MockBean
	private JwtDecoder jwtDecoder;

	@Autowired
	private PermitService permitService;

	@Test
	public void test001_aUserCreatedAFileSoAssignTheAllPermitsForTheOwner() {
		String userId = "1";
		String fileName = "test-file";

		Permit permit = permitService.addOwnerPermits(userId, fileName);

		assertEquals(userId, permit.getUserId(), "permission created for other user");
		assertEquals(fileName, permit.getFileName(), "permission created for other file");
		assertEquals(Permissions.RWX, permit.getRwx(), "the owner does not have all the permits");
	}

	@Test
	void test002_userCreatedFileWithSameNameShouldThrowConflict() {
		String userId = "1";
		String fileName = "test-file";

		permitService.addOwnerPermits(userId, fileName);
		assertThrowsExactly(ConflictException.class, () -> permitService.addOwnerPermits(userId, fileName));
	}

	@Test
	void test003_removePermitsFromOwnerIsForbidden() {
		String userId = "1";
		String fileName = "test-file";

		assertThrowsExactly(ForbiddenException.class,
				() -> permitService.removePermitsForSnippet(userId, fileName, userId, Permissions.RWX));
	}

	@Test
	void test004_sharePermissionToFileToOtherUser() {
		String ownerId = "1";
		String userId = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(ownerId, fileName);
		Permit permit = permitService.sharePermitsForSnippet(ownerId, fileName, userId, Permissions.RWX);

		assertEquals(userId, permit.getUserId(), "permission created for other user");
		assertEquals(ownerId, permit.getOwnerId(), "the owner is not the expected");
		assertEquals(fileName, permit.getFileName(), "permission created for other file");
		assertEquals(Permissions.RWX, permit.getRwx(), "the user does not have all the permits");
	}

	@Test
	void test005_sharePermissionsToOwner() {
		String ownerId = "1";
		String fileName = "test-file";

		assertThrowsExactly(ForbiddenException.class,
				() -> permitService.sharePermitsForSnippet(ownerId, fileName, ownerId, Permissions.RWX));
	}

	@Test
	void test006_shareAFileThatTheOwnerDoesNotHaveThrowForbidden() {
		String ownerId = "1";
		String userId = "2";
		String fileName = "test-file";

		assertThrowsExactly(ForbiddenException.class,
				() -> permitService.sharePermitsForSnippet(ownerId, fileName, userId, Permissions.RWX));
	}

	@Test
	void test007_removePermissionsFromUser() {
		String ownerId = "1";
		String userId = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(ownerId, fileName);
		permitService.sharePermitsForSnippet(ownerId, fileName, userId, Permissions.RWX);
		Permit permit = permitService.removePermitsForSnippet(ownerId, fileName, userId, Permissions.X);

		assertEquals(userId, permit.getUserId(), "permission created for other user");
		assertEquals(ownerId, permit.getOwnerId(), "the owner is not the expected");
		assertEquals(fileName, permit.getFileName(), "permission created for other file");
		assertEquals(Permissions.RW, permit.getRwx(), "The user has more or less permits that expected");
	}

	@Test
	void test008_removePermissionsFromUserWhereOwnerDoesNotHaveFileThenThrowForbidden() {
		String ownerId = "1";
		String userId = "2";
		String userThatRemovePermitsId = "3";
		String fileName = "test-file";

		permitService.addOwnerPermits(ownerId, fileName);
		permitService.sharePermitsForSnippet(ownerId, fileName, userId, Permissions.RWX);

		assertThrowsExactly(ForbiddenException.class,
				() -> permitService.removePermitsForSnippet(userThatRemovePermitsId, fileName, userId, Permissions.X));
	}

	@Test
	void test009_userHasPermissionWhenHasAllPermissionsThenTrue() {
		String ownerId = "1";
		String userId = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(ownerId, fileName);
		permitService.sharePermitsForSnippet(ownerId, fileName, userId, Permissions.RWX);

		assertTrue(permitService.userHasPermission(userId, ownerId, fileName, Permissions.RWX));
		assertTrue(permitService.userHasPermission(userId, ownerId, fileName, Permissions.R));
		assertTrue(permitService.userHasPermission(userId, ownerId, fileName, Permissions.W));
		assertTrue(permitService.userHasPermission(userId, ownerId, fileName, Permissions.X));
	}

	@Test
	void test010_userHasPermissionWhenHasSomePermissionsThenTrueAndFalse() {
		String ownerId = "1";
		String userId = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(ownerId, fileName);
		permitService.sharePermitsForSnippet(ownerId, fileName, userId, Permissions.RX);

		assertFalse(permitService.userHasPermission(userId, ownerId, fileName, Permissions.RWX));
		assertTrue(permitService.userHasPermission(userId, ownerId, fileName, Permissions.R));
		assertFalse(permitService.userHasPermission(userId, ownerId, fileName, Permissions.W));
		assertTrue(permitService.userHasPermission(userId, ownerId, fileName, Permissions.X));
	}

	@Test
	void test011_userGetAllSnippetsWhereHeHasReadPermitsWhenHeHas2SnippetsWithReadThenReturnTwo() {
		String userId = "1";
		String userId2 = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(userId, fileName + "1");
		permitService.addOwnerPermits(userId2, fileName + "2");

		permitService.sharePermitsForSnippet(userId2, fileName + "2", userId, Permissions.RX);

		Pageable pageable = PageRequest.of(0, 10);

		Page page = permitService.getAllReadableSnippets(userId, pageable);
		List<Permit> content = page.getContent();

		assertEquals(2, content.size());
	}

	@Test
	void test012_userGetAllSnippetsWhereHeHasReadPermitsWhenHeHas1SnippetsWithReadThenReturn1() {
		String userId = "1";
		String userId2 = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(userId, fileName + "1");
		permitService.addOwnerPermits(userId2, fileName + "2");

		Pageable pageable = PageRequest.of(0, 10);

		Page page = permitService.getAllReadableSnippets(userId, pageable);
		List<Permit> content = page.getContent();

		assertEquals(1, content.size());
	}

	@Test
	void test013_userGetAllSnippetsWhereHeHasReadPermitsWhenHeHas1RAnd1WThenReturn1() {
		String userId = "1";
		String userId2 = "2";
		String fileName = "test-file";

		permitService.addOwnerPermits(userId, fileName + "1");
		permitService.addOwnerPermits(userId2, fileName + "2");

		permitService.sharePermitsForSnippet(userId2, fileName + "2", userId, Permissions.X);

		Pageable pageable = PageRequest.of(0, 10);

		Page page = permitService.getAllReadableSnippets(userId, pageable);
		List<Permit> content = page.getContent();

		assertEquals(1, content.size());
	}
}
