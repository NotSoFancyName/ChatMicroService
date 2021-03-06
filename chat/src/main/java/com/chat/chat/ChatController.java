package com.chat.chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.json.*;
import org.keycloak.RSATokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
public class ChatController {
	  @Autowired
	  private Environment environment;
	  
	  @Autowired
	  private CommentRepository commentRepository;
	  
	  @Autowired
	  private MessageRepository messageRepository;
	  
	  @GetMapping("/dialog/aquire/service/{servise_id}/customer/{customer_id}")
	  public Object retrieveMessages
	    (@PathVariable Long servise_id, @PathVariable String customer_id,
	    		@RequestHeader(value="Authorization") String Authorization){
		  
		    if(!checkAuthority(trancate(Authorization),customer_id) && 
		    		!checkServiceOwner(trancate(Authorization),servise_id))
		  		return new ResponseEntity(HttpStatus.FORBIDDEN);
		  		    
		    List<Message> messages = messageRepository.findByServiceIdAndCustomerId(servise_id, customer_id);
		    
		    return messages;	
	  }	 
	  
	  @PostMapping("/dialog/save")
	  public ResponseEntity<Object> saveMessage(@RequestBody Message message,
			  @RequestHeader(value="Authorization") String Authorization) throws UnsupportedEncodingException{
		  
	  	if(!checkAuthority(trancate(Authorization),message.getCustomerId()) &&
	  			!checkServiceOwner(trancate(Authorization),message.getServiceId()))
	  		return new ResponseEntity(HttpStatus.FORBIDDEN);
		
		message.setTime(LocalDateTime.now());
		message.setMessageBody(new String(message.getMessageBody().getBytes("UTF-8"),"UTF-8"));
		
		System.out.println(message.getMessageBody());
		
		Message savedMessage = messageRepository.save(message);
		
		System.out.println(savedMessage.getMessageBody());
	  	
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
	  			.buildAndExpand(savedMessage.getId()).toUri();

	  	
	  	return ResponseEntity.created(location).build(); 
	  }
	  
	  @GetMapping("/dialog/aquire/all")
	  public List<Message> retrieveAllMessages(){
		    List<Message> messages = (List<Message>) messageRepository.findAll();
		    
		    for(Message m: messages) {
				System.out.println(m.getMessageBody());
		    }
		    
		    return messages;
	  }	 
	    
	  @PostMapping("/comments/save")
	  public Object saveComment(@RequestBody Comment comment,
			  @RequestHeader(value="Authorization") String Authorization){
		  
	  	if(!checkAuthority(trancate(Authorization),comment.getCustomerId()))
	  		return new ResponseEntity(HttpStatus.FORBIDDEN);
		  	
		comment.setTime(LocalDateTime.now());
		Comment savedComment = commentRepository.save(comment);
	  	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
	  			.buildAndExpand(savedComment.getId()).toUri();
	    
	  	/*
	     * Sending HTTP request to Services service
	     */ 
	  	sendRating(comment.getServiceId(),comment.getRating());
	  	return ResponseEntity.created(location).build();
	  }
	  
	  @GetMapping("/comments/aquire/all")
	  public List<Comment> retrieveAllComments()
	  {
		    List<Comment> comments = (List<Comment>) commentRepository.findAll();
		    return comments;
	  }	 
	  
	  @GetMapping("/comments/aquire/service/{service_id}")
	  public List<Comment> retrieveAllComments(@PathVariable Long service_id)
	  {
		    List<Comment> comments = (List<Comment>) commentRepository.findByServiceId(service_id);
		    return comments;
	  }	
	  
	  @DeleteMapping("/comments/delete/service/{service_id}/comment/{customer_id}")
	  public HttpStatus deleteCommentByCommentId(@PathVariable Long service_id,@PathVariable String customer_id)
	  {
		    commentRepository.deleteByCustomerIdAndServiceId(customer_id,service_id);
		    return HttpStatus.OK;
	  }	
	    
	  @DeleteMapping("/comments/delete/{inner_id}")
	  public HttpStatus deleteCommentById(@PathVariable Long inner_id)
	  {
		    commentRepository.deleteById(inner_id);
		    return HttpStatus.OK;
	  }	
	  
	  @DeleteMapping("/dialog/delete/{inner_id}")
	  public HttpStatus deleteMessageById(@PathVariable Long inner_id)
	  {
		    messageRepository.deleteById(inner_id);
		    return HttpStatus.OK;
	  }	
	  
	  @GetMapping("/dialog/aquire/service/{service_id}")
	  public Object retrieveDialogsWithCustomers
	    (@PathVariable Long service_id,@RequestHeader(value="Authorization") String Authorization){
		  	
		  	if(checkServiceOwner(trancate(Authorization),service_id))
		  		return  new ResponseEntity(HttpStatus.FORBIDDEN);
		 
		  		    
		    List<Message> messages = messageRepository.findByServiceIdOrderByIdDesc(service_id);
		    
		    HashSet<String> isPresent = new HashSet<String>();	
		    List<Message> firstMessagesInTheDialogs = new ArrayList<Message>();
		    
		    for(Message m:messages) {
		    	if(!isPresent.contains(m.getCustomerId())) {
		    		isPresent.add(m.getCustomerId());
		    		firstMessagesInTheDialogs.add(m);
		    	}
		    }
		    
		    return firstMessagesInTheDialogs;
	  }	
	  
	  @GetMapping("/dialog/aquire/customer/{customer_id}")
	  public Object retrieveDialogsWithServices
	    (@PathVariable String customer_id, 
	    		@RequestHeader(value="Authorization") String Authorization){
		  	
		    if(!checkAuthority(trancate(Authorization),customer_id))
		  		return  new ResponseEntity(HttpStatus.FORBIDDEN);
		    		    
		    List<Message> messages = messageRepository.findByCustomerIdOrderByIdDesc(customer_id);
		    
		    HashSet<Long> isPresent = new HashSet<Long>();	
		    List<Message> firstMessagesInTheDialogs = new ArrayList<Message>();
		    
		    for(Message m:messages) {
		    	if(!isPresent.contains(m.getServiceId())) {
		    		isPresent.add(m.getServiceId());
		    		firstMessagesInTheDialogs.add(m);
		    	}
		    }
		    
		    return firstMessagesInTheDialogs;
	  }	
	  	  
	  public Boolean checkAuthority(String tokenString,String customerId) {
		  	try
		  	{
		  	  AccessToken token = RSATokenVerifier.create(tokenString).getToken();
		  	  System.out.println(token.toString());
		  	  System.out.println(token.getSubject());
		  	  if(token.getSubject().equals(customerId))
		  		  return true;
		  	  else 
		  		  return false;
		  		  //return checkServiceOwner(serviceId, token.getSubject());
		  	}
		  	catch (VerificationException e)
		  	{
		  		e.printStackTrace();
		  		return false;
		  	}
	  }
	  
	  /*
	   * Delete 'Bearer' and spaces
	   */
	  
      public String trancate(String stringToBeTrancated) {		  
		  return stringToBeTrancated.substring(6).trim();
	  } 
	  
	  public String postRequestToSaveRating(String uri, String json) {   
			   String server = "http://35.244.186.40";
			   RestTemplate rest = new RestTemplate();
			   HttpHeaders headers = new HttpHeaders();
			   headers.add("Content-Type", "application/json");
			   headers.add("Accept", "*/*");
			   
				System.out.println(server + uri);
			   
			   HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
			   ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.POST, requestEntity, String.class);
			   HttpStatus status = responseEntity.getStatusCode();
			   
			   System.out.println(status);
			   System.out.println(responseEntity.getBody());
			   
			   return responseEntity.getBody();
		  }
	  
	  
	  public String getRequestToGetServiceInfo(String uri, Long serviceId) {
		  
		    String server = "http://35.244.186.40";
		    RestTemplate rest = new RestTemplate();
		    HttpHeaders headers = new HttpHeaders();
		    
		    headers.add("Content-Type", "application/json");
		    headers.add("Accept", "*/*");
		    
		    
		    HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		   
			System.out.println(requestEntity.getBody());	
			System.out.println(server + uri);
			
			
		    ResponseEntity<String> responseEntity = rest.exchange( String.format(server + uri,serviceId.toString()), 
		    		HttpMethod.GET, requestEntity, String.class);
		    
		    System.out.println(responseEntity.getBody());		
		    
		    return responseEntity.getBody();
		  }
	  
	  public ResponseEntity<String> sendRating(Long serviceId, Double rating) {	
			String url = String.format("/services/add_mark/%s/%s", serviceId, rating);
			try {
				JSONObject obj = new JSONObject(postRequestToSaveRating(url,""));
				if(obj.getInt("code") == HttpStatus.OK.value()){
					System.out.println("Status OK");
				}else {
					System.out.println("FAILED TO SEND RATING");
					throw new Exception();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			    return ResponseEntity
			            .status(HttpStatus.INTERNAL_SERVER_ERROR)
			            .body("Failed to sent raiting to Srvices Service");
			}
			return new ResponseEntity<String>(HttpStatus.OK);
	  }
	  
	  public Boolean checkServiceOwner(String tokenString,Long serviceId){	  
		  
		  try {
			JSONObject obj = new JSONObject(getRequestToGetServiceInfo("/services/id?id=%s",serviceId));
			String ownerId = obj.getJSONObject("result").getString("user_id");
			System.out.println(ownerId);
			return checkAuthority(tokenString, ownerId);
		  } catch (JSONException e) {
				e.printStackTrace();
				return false;
		  }
	  }
	  
	  
	  @GetMapping("/health")
	  public HttpStatus testHealth() {
	    return HttpStatus.OK;
	  }

	  @GetMapping("/liveness")
	  public HttpStatus testLiveness() {
	    return HttpStatus.OK;
	  }
}
