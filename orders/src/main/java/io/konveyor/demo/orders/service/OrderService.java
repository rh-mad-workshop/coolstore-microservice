package io.konveyor.demo.orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.konveyor.demo.orders.model.Order;
import io.konveyor.demo.orders.repository.OrderRepository;

@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrderRepository repository;

	/**
	 * Finds an {@link Order} using its {@code id} as search criteria
	 * @param id The {@link Order} {@code id}
	 * @return The {@link Order} with the supplied {@code id}, {@literal null} if no {@link Order} is found. 
	 */
//	@NewSpan
	public Order findById(Long id) {
		return repository.findById(id).orElse(null);
	}

//	@NewSpan
	public Page<Order> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
}
