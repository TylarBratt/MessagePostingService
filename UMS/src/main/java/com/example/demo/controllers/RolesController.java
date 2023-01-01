package com.example.demo.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.demo.dao.UmsRepository;
import com.example.demo.dtos.Constants;
import com.example.demo.dtos.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class RolesController {
	@Autowired
	private UmsRepository umsRepository;

	Map<String, Object> response = new HashMap<>();

	@GetMapping("role")
	public Mono<ResponseEntity<Map<String, Object>>> getAllRoles(@RequestParam String Token) {

		Map<String, Roles> roles = umsRepository.findAllRoles();
		if (roles == null) {
			response.put(Constants.CODE, "500");
			response.put(Constants.MESSAGE, "Roles have not been retrieved");
			response.put(Constants.DATA, new HashMap<>());
		} else {
			response.put(Constants.CODE, "200");
			response.put(Constants.MESSAGE, "List of Roles has been requested successfully");
			response.put(Constants.DATA, new ArrayList<>(roles.values()));
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}
	// String Request = "Update Role Table";
	// UUID userID = umsRepository.getIdByToken(Token);
	// if (umsRepository.permissionCheck(userID, Request)) {
	// }

	@PostMapping("role")
	public Mono<ResponseEntity<Map<String, Object>>> createRole(@RequestBody Roles role, @RequestBody String Token)
			throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			int worked = umsRepository.setRole(role);
			if (worked == 1) {
				response.put(Constants.CODE, "200");
				response.put(Constants.MESSAGE, "Role Created");
			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "Role Not Created");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@PutMapping("role")
	public Mono<ResponseEntity<Map<String, Object>>> updateRole(@RequestParam String roleId, @RequestBody Roles role,
			@RequestBody String Token) throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			int worked = umsRepository.updateRole(roleId, role);
			if (worked == 1) {
				response.put(Constants.CODE, "200");
				response.put(Constants.MESSAGE, "Role Updated");

			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "Role not Updated");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Token Invalid");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@DeleteMapping("role")
	public Mono<ResponseEntity<Map<String, Object>>> deleteRole(@RequestParam String roleId, @RequestBody String Token)
			throws SQLException {
		if (umsRepository.TokenCheck(Token)) {
			int worked = umsRepository.deleteRole(roleId);
			if (worked == 1) {
				response.put(Constants.CODE, "200");
				response.put(Constants.MESSAGE, "Role Deleted");

			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "Role not Deleted");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Token Invalid");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}
}