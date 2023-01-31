package com.example.demo.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.UmsRepository;
import com.example.demo.dtos.Constants;
import com.example.demo.dtos.Roles;
import com.example.demo.dtos.User;

import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class UserController {
	@Autowired
	private UmsRepository umsRepository;
	Map<String, Object> response = new HashMap<>();

	/*
	 * Logout section - /logout
	 * Get(getLastLogout), Post(setLogout)
	 */
	@GetMapping("logout")
	public Mono<ResponseEntity<Map<String, Object>>> getLastLogout(@RequestParam String Token) throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			Date logOut = umsRepository.getLastLogout(Token);
			if (logOut != null) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Logout has been Retrieved");
				response.put(Constants.DATA, logOut);
			} else {
				response.put(Constants.CODE, "404");
				response.put(Constants.MESSAGE, "Logout not been found");
				response.put(Constants.DATA, "null");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@PostMapping("logout")
	public Mono<ResponseEntity<Map<String, Object>>> setLogout(@RequestParam String Token) throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			Boolean done = umsRepository.setLogout(Token);
			if (done == true) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Succesfully updated logout");
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "Unsuccesful in updating logout");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	/*
	 * User Section- /user
	 * Get (getAllUsers), Post (createUser), put(updateUser), Delete(deleteUser)
	 */
	@GetMapping("user")
	public Mono<ResponseEntity<Map<String, Object>>> getAllUsers() {
		Map<UUID, User> users = umsRepository.findAllUsers();
		if (users == null) {
			response.put(Constants.CODE, "500");
			response.put(Constants.MESSAGE, "Users have not been retrieved");
			response.put(Constants.DATA, new HashMap<>());
		} else {
			response.put(Constants.CODE, "200");
			response.put(Constants.MESSAGE, "List of Users has been requested successfully");
			response.put(Constants.DATA, new ArrayList<>(users.values()));
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@PostMapping("user")
	public Mono<ResponseEntity<Map<String, Object>>> createUser(@RequestBody User user) throws SQLException {
		UUID userId = umsRepository.createUser(user);
		if (userId == null) {
			response.put(Constants.CODE, "500");
			response.put(Constants.MESSAGE, "User has not been created");
			response.put(Constants.DATA, "Check username for duplicates first");
		} else {
			response.put(Constants.CODE, "201");
			response.put(Constants.MESSAGE, "User created");
			response.put(Constants.DATA, userId.toString());
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@PutMapping("user")
	public Mono<ResponseEntity<Map<String, Object>>> updateUser(@RequestBody User NewUser, @RequestParam String Token)
			throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			UUID userID = umsRepository.getIdByToken(Token);
			Boolean worked = umsRepository.updateUser(userID, NewUser);
			if (worked == true) {
				response.put(Constants.CODE, "201");
				response.put(Constants.MESSAGE, "User updated");
			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "User not updated");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@DeleteMapping("user")
	public Mono<ResponseEntity<Map<String, Object>>> deleteUser(@RequestParam UUID UserID, @RequestParam String Token)
			throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			int worked = umsRepository.deleteUser(UserID);
			if (worked == 1) {
				response.put(Constants.CODE, "201");
				response.put(Constants.MESSAGE, "User deleted");
			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "User not deleted");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	// user role section-/user/role
	// Get (getRoleByToken), Post (assignRole), Delete(deleteRoleForUser)

	@GetMapping("user/role")
	public Mono<ResponseEntity<Map<String, Object>>> getRoleByToken(@RequestParam String Token) throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			UUID userID = umsRepository.getIdByToken(Token);
			Roles role = umsRepository.getRoleById(userID);
			if (userID == null) {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "User Not found");
				response.put(Constants.DATA, null);
			} else if (role != null) {
				response.put(Constants.CODE, "201");
				response.put(Constants.MESSAGE, "Role returned");
				response.put(Constants.DATA, role);
			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "Role Not Found");
				response.put(Constants.DATA, null);
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@PostMapping("user/role")
	public Mono<ResponseEntity<Map<String, Object>>> assignRole(@RequestBody Roles role, @RequestParam String Token)
			throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			UUID userID = umsRepository.getIdByToken(Token);
			Boolean worked = umsRepository.setRoleByID(userID, role);
			if (userID == null) {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "User Not found");
			} else if (worked) {
				response.put(Constants.CODE, "201");
				response.put(Constants.MESSAGE, "Role set");
			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "Error Updating Role");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}// end of AssignRole

	@DeleteMapping("user/role")
	public Mono<ResponseEntity<Map<String, Object>>> deleteRoleForUser(@RequestParam String Token) throws SQLException {
		if (umsRepository.TokenCheck(Token) == true) {
			UUID userID = umsRepository.getIdByToken(Token);
			int worked = umsRepository.deleteUser(userID);
			if (userID == null) {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "User Not found");
			} else if (worked == 1) {
				response.put(Constants.CODE, "201");
				response.put(Constants.MESSAGE, "User Deleted");
			} else {
				response.put(Constants.CODE, "500");
				response.put(Constants.MESSAGE, "Error Deleting User");
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}
	
	
	@GetMapping("user/producer")
	public Mono<ResponseEntity<Map<String, Object>>> getProducerForSubscriber(@RequestParam int SubscriberID, @RequestParam String Token) throws SQLException {
	if (umsRepository.TokenCheck(Token) == true) {
		List<Object> producers = umsRepository.getProducers(SubscriberID);
		if (producers == null) {
			response.put(Constants.CODE, "500");
			response.put(Constants.MESSAGE, "Producers Not found");
		} else if (producers != null) {
			response.put(Constants.CODE, "201");
			response.put(Constants.MESSAGE, "Producers found");
			response.put(Constants.DATA, producers);
		}
	} else {
		response.put(Constants.CODE, "400");
		response.put(Constants.MESSAGE, "Invalid Token");
	}
	return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
			.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}
	
	

	public class ParameterStringBuilder {
		public static String getParamsString(Map<String, String> params)
				throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				result.append("&");
			}

			String resultString = result.toString();
			return resultString.length() > 0
					? resultString.substring(0, resultString.length() - 1)
					: resultString;
		}
	}

}