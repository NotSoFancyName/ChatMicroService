package com.chat.chat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Message {
	    @Id
	    @GeneratedValue(strategy=GenerationType.AUTO)
	    private Long id;
	    
	    private Long serviceId;
		private String customerId;
	    
	    private LocalDateTime time;
	    
	    /*if true - sent from service provider
	     *if false - sent from customer */
	    private Boolean fromServiceProvider;
	    
	    private String messageBody;
	    
	    Message(){}
	    
	    Message(Long serviceId, String customerId, String messageBody, Boolean fromServiceProvider){
	    	this.serviceId = serviceId;
	    	this.customerId = customerId;
	    	this.setMessageBody(messageBody);
	    	this.setTime(LocalDateTime.now());
	    	this.fromServiceProvider = fromServiceProvider;
	    }

		public Boolean getFromServiceProvider() {
			return fromServiceProvider;
		}

		public void setFromServiceProvider(Boolean fromServiceProvider) {
			this.fromServiceProvider = fromServiceProvider;
		}

		public String getMessageBody() {
			return messageBody;
		}

		public void setMessageBody(String messageBody) {
			this.messageBody = messageBody;
		}

		public LocalDateTime getTime() {
			return time;
		}

		public void setTime(LocalDateTime time) {
			this.time = time;
		}
		
		public Long getId(){
			return id;
		}
		
		public Long getServiceId() {
			return serviceId;
		}
		public void setServiceId(Long serviceId) {
			this.serviceId = serviceId;
		}
		public String getCustomerId() {
			return customerId;
		}
		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
}
