package com.example.demo.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class User {
    UUID id;
    String name;
    String email;
    String password;
    int created;
    List<Roles> roles = new ArrayList<>();
    LastSession lastSession;



	public UUID getId() {
		return id;
	}



	public void setId(UUID id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public int getCreated() {
		return created;
	}



	public void setCreated(int created) {
		this.created = created;
	}



	public List<Roles> getRoles() {
		return roles;
	}



	public void setRoles(List<Roles> roles) {
		this.roles = roles;
	}



	public LastSession getLastSession() {
		return lastSession;
	}



	public void setLastSession(LastSession lastSession) {
		this.lastSession = lastSession;
	}



	public User(UUID id, String name, String email, String password, int created, List<Roles> roles,
			LastSession lastSession) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.created = created;
		this.roles = roles;
		this.lastSession = lastSession;
	}



	public User() {
		id = null;
		name = null;
		email = null;
		password = null;
		created = 0;
		roles = null;
		lastSession = null;
	}



	public void addRole(Roles role) {
        this.roles.add(role);
    }

}