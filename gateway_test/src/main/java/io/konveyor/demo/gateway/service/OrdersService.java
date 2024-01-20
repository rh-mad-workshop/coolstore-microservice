package io.konveyor.demo.gateway.service;

import java.util.List;

import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.repository.CustomerRepository;
import io.konveyor.demo.gateway.repository.InventoryRepository;
import io.konveyor.demo.gateway.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	public Order getById(Long id) {		
		Order o = orderRepository.getOrderById(id);
		if (o != null) {
			o.setCustomer(customerRepository.getCustomerById(o.getCustomer().getId()));
			o.setItems(inventoryRepository.getProductDetails(o.getItems()));
		}
		return o;
	}

	public Page<Order> findAll(Pageable pageable) {
		List<Order> orders = orderRepository.findAll(pageable);
		for (Order o : orders) {
			o.setCustomer(customerRepository.getCustomerById(o.getCustomer().getId()));
			o.setItems(inventoryRepository.getProductDetails(o.getItems()));
		}
		return new PageImpl<Order>(orders, pageable, orders.size());
	}	

}
