package com.chat.chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	  private MessegeRepository messegeRepository;
	  
	  private final Long maxTriesQuantity = 30L;
	  
	  @GetMapping("/dialog/aquire/service/{servise_id}/customer/{customer_id}")
	  public List<Messege>  retrieveMesseges
	    (@PathVariable Long servise_id, @PathVariable String customer_id){
		  		    
		    List<Messege> messeges = messegeRepository.findByServiceIdAndCustomerId(servise_id, customer_id);
		    return messeges;
	  }	 
	  
	  @PostMapping("/dialog/save")
	  public ResponseEntity<Object> saveMessege(@RequestBody Messege messege){
		messege.setTime(LocalDateTime.now());
		Messege savedMessege = messegeRepository.save(messege);
	  	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
	  			.buildAndExpand(savedMessege.getId()).toUri();
	  	return ResponseEntity.created(location).build();
	  }
	  
	  @GetMapping("/dialog/aquire/all")
	  public List<Messege> retrieveAllMesseges(){
		    List<Messege> messeges = (List<Messege>) messegeRepository.findAll();
		    return messeges;
	  }	 
	  
	  
	  @PostMapping("/comments/save")
	  public ResponseEntity<Object> saveComment(@RequestBody Comment comment){
		comment.setTime(LocalDateTime.now());
		Comment savedComment = commentRepository.save(comment);
	  	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
	  			.buildAndExpand(savedComment.getId()).toUri();
	    
	  	/*
	     * Sending HTTP request to Services microservice
	     */ 
		String url = String.format("https://localhost:8762/api/sevices/sevices/add_mark/%s/%s",
				comment.getServiceId(),comment.getRating());
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			String urlParameters = "";
			
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			if(con.getResponseCode() == HttpStatus.OK.ordinal()){
				System.out.println("Status OK");
			}else {
				System.out.println("Failed to sent raiting to Srvice Service");
				throw new IOException();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		    return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body("Failed to sent raiting to Srvice Service");
		}
		
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
	  public HttpStatus deleteMessegeById(@PathVariable Long inner_id)
	  {
		    messegeRepository.deleteById(inner_id);
		    return HttpStatus.OK;
	  }	
	  
	  @GetMapping("/dialog/aquire/service/{service_id}")
	  public List<Messege>  retrieveDialogsWithCustomers
	    (@PathVariable Long service_id){
		  		    
		    List<Messege> messeges = messegeRepository.findByServiceIdOrderByIdDesc(service_id);
		    
		    HashSet<String> isPresent = new HashSet<String>();	
		    List<Messege> firstMessagesInTheDialogs = new ArrayList<Messege>();
		    
		    for(Messege m:messeges) {
		    	if(!isPresent.contains(m.getCustomerId())) {
		    		isPresent.add(m.getCustomerId());
		    		firstMessagesInTheDialogs.add(m);
		    	}
		    }
		    
		    return firstMessagesInTheDialogs;
	  }	
}
