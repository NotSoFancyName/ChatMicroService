package com.chat.chat;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.chat.chat.Messege;

public interface MessegeRepository extends CrudRepository<Messege, Long>{
	List<Messege> findByServiceIdAndCustomerId(Long serviceId, Long customerId);
	List<Messege> findByServiceIdOrderByIdDesc(Long serviceId);
}
