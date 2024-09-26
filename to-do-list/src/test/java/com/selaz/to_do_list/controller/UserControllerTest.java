package com.selaz.to_do_list.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.selaz.to_do_list.model.User;
import com.selaz.to_do_list.repository.UserRepository;

public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllUsers() {
		User user1 = new User(1L, "user1", "nivel1");
		User user2 = new User(2L, "user2", "nivel2");
		when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

		List<User> users = userController.getAllUsers();
		assertEquals(2, users.size());
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testCreateUser() {
		User user = new User(null, "newUser", "nivelNew");
		when(userRepository.save(any(User.class))).thenReturn(user);

		User createdUser = userController.createUser(user);
		assertNotNull(createdUser);
		assertEquals("newUser", createdUser.getUsername());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testUpdateUser_UserExists() {
		User oldUser = new User(1L, "oldUser", "nivelOld");
		User newUser = new User(null, "updatedUser", "nivelUpdated");
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
		when(userRepository.save(any(User.class))).thenReturn(oldUser);

		ResponseEntity<User> response = userController.updateUser(1L, newUser);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("updatedUser", response.getBody().getUsername());
		verify(userRepository, times(1)).findById(1L);
		verify(userRepository, times(1)).save(oldUser);
	}

	@Test
	void testUpdateUser_UserNotFound() {
		User newUser = new User(null, "updatedUser", "nivelUpdated");
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		ResponseEntity<User> response = userController.updateUser(1L, newUser);
		assertEquals(404, response.getStatusCodeValue());
		verify(userRepository, times(1)).findById(1L);
	}

	@Test
	void testDeleteUser_UserExists() {
		User userToDelete = new User(1L, "userToDelete", "nivelToDelete");
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(userToDelete));

		ResponseEntity<Void> response = userController.deleteUser(1L);
		assertEquals(204, response.getStatusCodeValue());
		verify(userRepository, times(1)).findById(1L);
		verify(userRepository, times(1)).delete(userToDelete);
	}

	@Test
	void testDeleteUser_UserNotFound() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		ResponseEntity<Void> response = userController.deleteUser(1L);
		assertEquals(404, response.getStatusCodeValue());
		verify(userRepository, times(1)).findById(1L);
	}

}
