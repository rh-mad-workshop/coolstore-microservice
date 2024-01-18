package io.konveyor.demo.orders.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import io.konveyor.demo.orders.model.Order;
import io.konveyor.demo.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.opentracing.Span;
import io.opentracing.Tracer;

@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrderRepository repository;
	
	@Autowired
	Tracer tracer;
	
	/**
	 * Finds an {@link Order} using its {@code id} as search criteria
	 * @param id The {@link Order} {@code id}
	 * @return The {@link Order} with the supplied {@code id}, {@literal null} if no {@link Order} is found. 
	 */
	public Order findById(Long id) {
		Span span = tracer.buildSpan("findById").start();
		Optional<Order> o = repository.findById(id);
		try {
			Order order = o.get();
			//Force lazy loading of the OrderItem list
			order.getItems().size();
			return order;
		} catch (NoSuchElementException nsee) {
			return null;
		} finally {
			span.finish();
		}
	}
	
	public Page<Order>findAll(Pageable pageable) {
		Page<Order> p = repository.findAll(pageable);
		return p;
	}
}
