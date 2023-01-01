package com.example.demo.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dao.UmsRepository;
import com.example.demo.dtos.Constants;

import reactor.core.publisher.Mono;

@RestController
public class LoginController {
	@Autowired
	private UmsRepository umsRepository;

	Map<String, Object> myResponse = new HashMap<>();

	@GetMapping("/")
	public String GetHome(@RequestParam String Token) throws SQLException {
		String username = null;
		String returnString = null;
		if (umsRepository.TokenCheck(Token)) {
			UUID userId = umsRepository.getIdByToken(Token);
			username = umsRepository.getUserName(userId);
			returnString = "<HTML><body>Logged in as: " + username + "\n"
					+ "</body></HTML>";
		} else {
			returnString = "<HTML><body>You are not signed in, please click the link to sign in. <br><a href='https://github.com/login/oauth/authorize?response_type=code&client_id=2d9b4247207aa7aea588&scope=user:email%20read:user%27&state=u2KMo0Pi6sya2liUwEwiZnc1VrMR0vnLOiFS5usmUqc%3D&redirect_uri=http://localhost:8080/login/oauth2/code/github'> Login with Github</a><div class=\"container authenticated\" style=\"display:none\">\n";
		}
		return returnString;
	}

	@GetMapping("login/oauth2/code/github")
	public Mono<ResponseEntity<Map<String, Object>>> userCallBack(@RequestParam String code, @RequestParam String state)
			throws SQLException {
		// Ok this was incredibly complex to figure out but this will query github
		// properly and return in this case an access_Token.
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		// This next section encodes our clientID and our ClientSecret
		String credentials = "2d9b4247207aa7aea588:bc8884d75f3b1212ec84a35a3ea0c2893f5dedbc";
		String encodedCredentials = new String(Base64.getEncoder().encode(credentials.getBytes()));
		// this section creates headers for the url and attaches the encoded credentials
		// to it and then to the url
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);
		// adds parameters and builds the URL
		String access_token_url = "https://github.com/login/oauth/access_token";
		access_token_url += "?code=" + code;
		access_token_url += "&grant_type=authorization_code";
		access_token_url += "&redirect_uri=http://localhost:8080/login/oauth2/code/github";
		// this sends the request and saves the JSON data to the response Entity
		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

		System.out.println("Access Token Response ---------" + response.getBody());

		// here we parse through the json data.
		String JsonString = response.getBody();
		String[] parse1 = JsonString.split("en\":\"");
		String[] parse2 = parse1[1].split("\",\"to");
		String accessToken = parse2[0];
		System.out.println(accessToken);

		// here we query the Github API for a second time with the Access token in
		// exchange for user data.
		RestTemplate restTemplate2 = new RestTemplate();
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + accessToken);
		HttpEntity<String> request2 = new HttpEntity<>("parameters", headers2);
		ResponseEntity<String> response2 = restTemplate2.exchange("https://api.github.com/user", HttpMethod.GET,
				request2, String.class);
		System.out.println(response2.getBody());

		// here we parse the final response to get the username.

		String JsonString2 = response2.getBody();
		String[] parse3 = JsonString2.split("in\":\"");
		String[] parse4 = parse3[1].split("\",\"id");
		String username = parse4[0];
		System.out.println(username);

		// we will now save the username and access code to the database
		Date date = new Date();
		java.sql.Date formattedDate = new java.sql.Date(date.getTime());

		int authId = umsRepository.getAuthId();
		int worked = umsRepository.setAuth(authId, username, accessToken, formattedDate);

		// finally, we need to send data to the next api.
		HttpURLConnection connection = null;
		try {
			String base = "http://localhost:8082/receive?";

			String parameters = "ACCESSCODE='" + accessToken + "'&USERNAME=" + username;
			URL url = new URL(base + parameters);
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");

			connection.connect();
			connection.getErrorStream();
			connection.getInputStream();
			connection.disconnect();

		} catch (MalformedURLException e) {
			worked = 0;
			e.printStackTrace();
		} catch (IOException e) {
			worked = 0;
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		// this is the algorithm to return whether the check passed or failed.
		if (worked == 1) {
			myResponse.put(Constants.CODE, "200");
			myResponse.put(Constants.MESSAGE, "Authorizing");

		} else {
			myResponse.put(Constants.CODE, "500");
			myResponse.put(Constants.MESSAGE, "Error: Not Authorizing");

		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(myResponse));
	}
}
