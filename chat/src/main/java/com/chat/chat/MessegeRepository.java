package com.chat.chat;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.chat.chat.Messege;

public interface MessegeRepository extends CrudRepository<Messege, Long>{
	List<Messege> findByServiceIdAndCustomerId(Long serviceId, String customerId);
	List<Messege> findByServiceId(Long serviceId);
	List<Messege> findByServiceIdOrderByIdDesc(Long serviceId);
	void deleteById(Long id);
}
