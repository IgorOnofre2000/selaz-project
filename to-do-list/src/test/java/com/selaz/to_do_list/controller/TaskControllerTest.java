package com.selaz.to_do_list.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.selaz.to_do_list.dto.TaskDto;
import com.selaz.to_do_list.model.Status;
import com.selaz.to_do_list.model.Task;
import com.selaz.to_do_list.model.User;
import com.selaz.to_do_list.repository.TaskRepository;
import com.selaz.to_do_list.repository.UserRepository;

public class TaskControllerTest {

	@InjectMocks
	private TaskController taskController;

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private Authentication authentication;

	@Mock
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getPrincipal()).thenReturn(userDetails);
	}

	@Test
	void testGetAllTasks_NoFilters() {
		User user = new User(1L, "testUser", "nivel1");
		when(userDetails.getUsername()).thenReturn("testUser");
		when(userRepository.findByUsername("testUser")).thenReturn(user);
		when(taskRepository.findByUser(user)).thenReturn(Arrays.asList(new Task(), new Task()));

		List<Task> tasks = taskController.getAllTasks(null, null);
		assertEquals(2, tasks.size());
		verify(taskRepository, times(1)).findByUser(user);
	}

	@Test
	void testGetAllTasks_WithStatusFilter() {
		when(userDetails.getUsername()).thenReturn("testUser");
		when(userRepository.findByUsername("testUser")).thenReturn(new User());
		when(taskRepository.findByStatus(Status.CONCLUIDA)).thenReturn(Arrays.asList(new Task()));

		List<Task> tasks = taskController.getAllTasks("CONCLUIDA", null);
		assertEquals(1, tasks.size());
		verify(taskRepository, times(1)).findByStatus(Status.CONCLUIDA);
	}

	@Test
	void testGetAllTasks_WithSortByDueDate() {
		when(userDetails.getUsername()).thenReturn("testUser");
		when(userRepository.findByUsername("testUser")).thenReturn(new User());
		when(taskRepository.findAllByOrderByDueDate()).thenReturn(Arrays.asList(new Task()));

		List<Task> tasks = taskController.getAllTasks(null, "dueDate");
		assertEquals(1, tasks.size());
		verify(taskRepository, times(1)).findAllByOrderByDueDate();
	}

	@Test
	void testCreateTask_UserExists() {
		TaskDto taskDto = new TaskDto();
		taskDto.setTitle("New Task");
		taskDto.setDescription("Task Description");
		taskDto.setDueDate(new Date());
		taskDto.setStatus(Status.PENDENTE);
		taskDto.setUser_id(1L);

		User user = new User(1L, "testUser", "nivel1");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(taskRepository.save(any(Task.class))).thenReturn(new Task());

		ResponseEntity<Task> response = taskController.createTask(taskDto);
		assertEquals(200, response.getStatusCodeValue());
		verify(taskRepository, times(1)).save(any(Task.class));
	}

	@Test
	void testCreateTask_UserNotFound() {
		TaskDto taskDto = new TaskDto();
		taskDto.setUser_id(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseEntity<Task> response = taskController.createTask(taskDto);
		assertEquals(404, response.getStatusCodeValue());
		verify(taskRepository, never()).save(any(Task.class));
	}

	@Test
	void testUpdateTask_TaskExists() {
		TaskDto taskDto = new TaskDto();
		taskDto.setTitle("Updated Task");
		taskDto.setDescription("Updated Description");
		taskDto.setDueDate(new Date());
		taskDto.setStatus(Status.EM_ANDAMENTO);

		Task existingTask = new Task(1L, new User(), "Old Task", "Old Description", new Date(), new Date(),
				Status.PENDENTE);
		when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
		when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

		ResponseEntity<Task> response = taskController.updateTask(1L, taskDto);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Updated Task", response.getBody().getTitle());
		verify(taskRepository, times(1)).save(existingTask);
	}

	@Test
	void testUpdateTask_TaskNotFound() {
		TaskDto taskDto = new TaskDto();

		when(taskRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseEntity<Task> response = taskController.updateTask(1L, taskDto);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	void testDeleteTask_TaskExists() {
		Task taskToDelete = new Task(1L, new User(), "Task to Delete", "Description", new Date(), new Date(),
				Status.PENDENTE);
		when(taskRepository.findById(1L)).thenReturn(Optional.of(taskToDelete));

		ResponseEntity<Void> response = taskController.deleteTask(1L);
		assertEquals(204, response.getStatusCodeValue());
		verify(taskRepository, times(1)).delete(taskToDelete);
	}

	@Test
	void testDeleteTask_TaskNotFound() {
		when(taskRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseEntity<Void> response = taskController.deleteTask(1L);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	void testGetAllTasksByUser_UserExists() {
		User user = new User(1L, "testUser", "nivel1");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(Arrays.asList(new Task()));

		List<Task> tasks = taskController.getAllTasksByUser(1L);
		assertEquals(1, tasks.size());
		verify(taskRepository, times(1)).findByUser(user);
	}

	@Test
	void testGetAllTasksByUser_UserNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		List<Task> tasks = taskController.getAllTasksByUser(1L);
		assertNull(tasks);
	}

}
