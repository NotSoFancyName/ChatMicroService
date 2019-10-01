package com.chat.chat;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.chat.chat.Message;

public interface MessageRepository extends CrudRepository<Message, Long>{
	List<Message> findByServiceIdAndCustomerId(Long serviceId, String customerId);
	List<Message> findByServiceId(Long serviceId);
	List<Message> findByServiceIdOrderByIdDesc(Long serviceId);
	List<Message> findByCustomerIdOrderByIdDesc(String CustomerId);
	void deleteById(Long id);
}
