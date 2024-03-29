package io.konveyor.demo.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.repository.CustomerRepository;
import io.konveyor.demo.gateway.repository.InventoryRepository;
import io.konveyor.demo.gateway.repository.OrderRepository;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdersService {
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;

	@NewSpan
	public Order getById(@SpanTag Long id) {
		log.debug("Entering OrdersService.getById()");
		Order o = orderRepository.getOrderById(id);

		if (o != null) {
			o.setCustomer(customerRepository.getCustomerById(o.getCustomer().getId()));
			o.setItems(inventoryRepository.getProductDetails(o.getItems()));
		}

		return o;
	}

	@NewSpan
	public Page<Order> findAll(@SpanTag Pageable pageable) {
		log.debug("Entering OrdersService.findAll()");
		var orders = orderRepository.findAll(pageable);

		orders.forEach(o -> {
			o.setCustomer(customerRepository.getCustomerById(o.getCustomer().getId()));
			o.setItems(inventoryRepository.getProductDetails(o.getItems()));
		});

		return new PageImpl<>(orders, pageable, orders.size());
	}	
}
