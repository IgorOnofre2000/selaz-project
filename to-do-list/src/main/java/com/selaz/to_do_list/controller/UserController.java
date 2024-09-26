package com.selaz.to_do_list.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selaz.to_do_list.model.User;
import com.selaz.to_do_list.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Operation(summary = "List all users", description = "Retrieve a list of all users.")
	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Operation(summary = "Create a new user", description = "Create a new user and save it in the database.")
	@PostMapping
	public User createUser(@RequestBody User user) {
		return userRepository.save(user);
	}

	@Operation(summary = "Update an existing user", description = "Update a user's details using their ID.")
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User newUser) {
		Optional<User> oldUser = userRepository.findById(id);

		if (!oldUser.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		User user = oldUser.get();
		user.setUsername(newUser.getUsername());
		user.setNivel(newUser.getNivel());

		final User updatedUser = userRepository.save(user);
		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "Delete a user", description = "Delete a user using their ID.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		Optional<User> userToDelete = userRepository.findById(id);

		if (!userToDelete.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		userRepository.delete(userToDelete.get());
		return ResponseEntity.noContent().build();
	}

}
