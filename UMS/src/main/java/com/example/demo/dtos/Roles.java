package com.example.demo.dtos;

import java.util.UUID;

public class Roles {
	String roleId;

	String roleName;
	String description;

	public Roles(String roleId2, String string, String string2) {
		roleId = roleId2;
		roleName = string;
		description = string2;
	}

	public Roles(UUID i, String string, String string2) {
		roleId = i.toString();
		roleName = string;
		description = string2;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getRole() {
		Roles role = new Roles(roleId, roleName, description);
		return role;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

}
