package com.chat.chat;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment,Long>{
	List<Comment> findByServiceIdAndCustomerId(Long serviceId, String customerId);
	List<Comment> findByServiceId(Long serviceId);
	void deleteByCustomerIdAndServiceId(String customerId, Long serviceId);
}
