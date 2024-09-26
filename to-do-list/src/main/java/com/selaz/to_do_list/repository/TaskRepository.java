package com.selaz.to_do_list.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.selaz.to_do_list.model.Status;
import com.selaz.to_do_list.model.Task;
import com.selaz.to_do_list.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByStatus(Status status);

	List<Task> findAllByOrderByDueDate();

	List<Task> findByUser(User user);
}
