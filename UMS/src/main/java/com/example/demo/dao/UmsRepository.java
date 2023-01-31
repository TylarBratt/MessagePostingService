package com.example.demo.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.dtos.Roles;
import com.example.demo.dtos.User;

public interface UmsRepository {

	Map<UUID, User> findAllUsers();

	Map<String, Roles> findAllRoles();

	User findUserByID(UUID userId);

	UUID createUser(User user);

	int deleteUser(UUID userId);

	Boolean TokenCheck(String token);

	Map<Integer, String> getProducerId(String subscriberID);

	Date getLastLogout(String token);

	Boolean setLogout(String token);

	Boolean updateUser(UUID userID, User newUser);

	UUID getIdByToken(String token);

	Roles getRoleById(UUID userID);

	Boolean setRoleByID(UUID userID, Roles role);

	int setRole(Roles role);

	int updateRole(String roleId, Roles role);

	int deleteRole(String roleId);

	int getAuthId();

	int setAuth(int authId, String username, String accessToken, Date formattedDate);

	String getUserName(UUID userId);
	
	List<Object> getProducers(int Subscriber);
}
