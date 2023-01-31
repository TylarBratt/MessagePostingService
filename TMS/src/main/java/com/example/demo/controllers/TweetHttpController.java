package com.example.demo.controllers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dao.MessageRepository;
import com.example.demo.dtos.Constants;
import com.example.demo.dtos.Message;
import com.example.demo.dtos.Tweet;

import reactor.core.publisher.Mono;
	
@RestController
public class TweetHttpController {
	private MessageRepository messageRepository;
	Map<String, Object> response = new HashMap<>();
	
	@GetMapping("receive")
	public Mono<ResponseEntity<Map<String, Object>>> getUUID(@RequestParam String ACCESSTOKEN, @RequestParam String USERNAME) throws SQLException{	
			UUID newID = UUID.randomUUID();
			int Execute = messageRepository.setToken(newID, USERNAME, ACCESSTOKEN);  
			if (Execute == 1) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Auth created");
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "Message could not be found");
			}
			return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
					.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}

	@GetMapping("message")
	public Mono<ResponseEntity<Map<String, Object>>> GetTweetByID(@RequestParam String Token, @RequestParam UUID messageID) throws SQLException{
		if (messageRepository.TokenCheck(Token) == true) {
			Message message = messageRepository.getMessagebyId(messageID);
			if (message != null) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Message has been retrieved");
				response.put(Constants.DATA, message);
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "Message could not be found");
				response.put(Constants.DATA, new HashMap<>());
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	
	}//end of GetTweetByUUID

	@PostMapping("message")
	public Mono<ResponseEntity<Map<String, Object>>> CreateTweet(@RequestParam String Token, @RequestBody String data) throws SQLException {
		if (messageRepository.TokenCheck(Token) == true) {
			UUID id = UUID.randomUUID();
			
			UUID userID = messageRepository.getIDByToken(Token);
			Date date = new Date();
			
			
		Message message = new Message(id, userID, data, date);
		
			if (message.getContent() != null) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Message has been created");
				response.put(Constants.DATA, message);
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "Message could not be created");
				response.put(Constants.DATA, new HashMap<>());
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	
	}//end of CreateTweet
		
	@PutMapping("message")
	public Mono<ResponseEntity<Map<String, Object>>> UpdateTweet(@RequestParam String Token, @RequestBody UUID id, @RequestBody String NewData) throws SQLException {
		if (messageRepository.TokenCheck(Token) == true) {
			int alpha = messageRepository.updateMessage(id, NewData);
		
			if (alpha == 1) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Message has been updated");
				
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "Message could not be updated");
				response.put(Constants.DATA, new HashMap<>());
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	
	}//end of UpdateUser
	
	@DeleteMapping("message")
	public Mono<ResponseEntity<Map<String, Object>>> DeleteTweet(@RequestParam String Token, @RequestParam UUID TweetID) throws SQLException {
		if (messageRepository.TokenCheck(Token) == true) {
			int alpha = messageRepository.deleteMessageById(TweetID);
			if (alpha == 1) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Message has been deleted");
				
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "Message could not be deleted");
				response.put(Constants.DATA, new HashMap<>());
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}
	
	@GetMapping("message/producer")
	public Mono<ResponseEntity<Map<String, Object>>> getProducerbySubscriber(@RequestParam String Token, @RequestParam String SubscriberID) throws SQLException{
		if (messageRepository.TokenCheck(Token) == true) {
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> httpResponse = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<String> request = new HttpEntity<String>(headers);
			String url = "http://localhost:8080/user/producer?";
					url += "SubscriberID='" + SubscriberID + "'&Token=" + Token;
			httpResponse = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			System.out.println("Response ---------" + httpResponse.getBody());
			
			// here we parse through the json data. This section still needs to be looked at.
			String JsonString = httpResponse.getBody();
			String[] parse1 = JsonString.split("en\":\"");
			String[] parse2 = parse1[1].split("\",\"to");
			
			String producers = parse2[0];
			System.out.println(producers);
			
			if (producers != null) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "ProducerIDs have been Retrieved");
				response.put(Constants.DATA, producers);
			} else {
				response.put(Constants.CODE, 500);
				response.put(Constants.MESSAGE, "ProducerIDs have not been Retrieved");
				response.put(Constants.DATA, new HashMap<>());
			}
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	
	}//end of GetTweetByUUID
	
	@GetMapping("auth")
	public Mono<ResponseEntity<Map<String, Object>>> AuthSession(@RequestParam String Token) throws SQLException{
		if (messageRepository.TokenCheck(Token) == true) {
				response.put(Constants.CODE, 200);
				response.put(Constants.MESSAGE, "Token Authorized");	
		} else {
			response.put(Constants.CODE, "400");
			response.put(Constants.MESSAGE, "Invalid Token");
		}
		return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
				.header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
	}//end of AuthSession

	 public static Tweet[] addX(int n, Tweet arr[], Tweet x)
	   {
	       int i;
	   
	       // create a new array of size n+1
	       Tweet newarr[] = new Tweet[n + 1];
	   
	       // insert the elements from
	       // the old array into the new array
	       // insert all elements till n
	       // then insert x at n+1
	       for (i = 0; i < n; i++)
	           newarr[i] = arr[i];
	   
	       newarr[n] = x;
	   
	       return newarr;
	   }
	}//end of HttpController Class
