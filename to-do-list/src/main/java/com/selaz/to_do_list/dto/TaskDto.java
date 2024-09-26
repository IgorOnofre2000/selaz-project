package com.selaz.to_do_list.dto;

import java.io.Serializable;
import java.util.Date;

import com.selaz.to_do_list.model.Status;

import jakarta.validation.constraints.NotNull;

public class TaskDto implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotNull
	private Long user_id;

	private String title;
	private String description;
	private Date dueDate;
	private Status status;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
