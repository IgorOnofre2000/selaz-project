package com.selaz.to_do_list.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.selaz.to_do_list.dto.TaskDto;
import com.selaz.to_do_list.model.Status;
import com.selaz.to_do_list.model.Task;
import com.selaz.to_do_list.model.User;
import com.selaz.to_do_list.repository.TaskRepository;
import com.selaz.to_do_list.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Operation(summary = "List all tasks", description = "Retrieve all tasks with optional filters for status and sorting by due date.")
	@GetMapping
	public List<Task> getAllTasks(@RequestParam(required = false) String status,
			@RequestParam(required = false) String sort) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();

		User currentUser = userRepository.findByUsername(username);

		if (status != null) {
			Status taskStatus = Status.valueOf(status.toUpperCase());
			return taskRepository.findByStatus(taskStatus);
		} else if ("dueDate".equals(sort)) {
			return taskRepository.findAllByOrderByDueDate();
		} else {
			return taskRepository.findByUser(currentUser);
		}
	}

	@Operation(summary = "Create a new task", description = "Create a new task and save it in the database.")
	@PostMapping
	public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
		Optional<User> user = userRepository.findById(taskDto.getUser_id());

		if (!user.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Task taskToSave = new Task();
		taskToSave.setUser(user.orElse(null));
		taskToSave.setTitle(taskDto.getTitle());
		taskToSave.setDescription(taskDto.getDescription());
		taskToSave.setCreatedAt(new Date());
		taskToSave.setDueDate(taskDto.getDueDate());
		taskToSave.setStatus(taskDto.getStatus());

		final Task savedTask = taskRepository.save(taskToSave);
		return ResponseEntity.ok(savedTask);
	}

	@Operation(summary = "Update an existing task", description = "Update a task's details using its ID.")
	@PutMapping("/{id}")
	public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
		Optional<Task> oldTask = taskRepository.findById(id);

		if (!oldTask.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Task task = oldTask.get();
		task.setTitle(taskDto.getTitle());
		task.setDescription(taskDto.getDescription());
		task.setDueDate(taskDto.getDueDate());
		task.setStatus(taskDto.getStatus());

		final Task updatedTask = taskRepository.save(task);
		return ResponseEntity.ok(updatedTask);
	}

	@Operation(summary = "Delete a task", description = "Delete a task using its ID.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		Optional<Task> taskToDelete = taskRepository.findById(id);

		if (!taskToDelete.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		taskRepository.delete(taskToDelete.get());
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "List tasks of a specific user", description = "Retrieve all tasks assigned to a specific user by their user ID.")
	@GetMapping("/{userId}")
	public List<Task> getAllTasksByUser(@PathVariable Long userId) {
		Optional<User> user = userRepository.findById(userId);

		if (!user.isPresent()) {
			return null;
		}

		return taskRepository.findByUser(user.get());
	}

}
