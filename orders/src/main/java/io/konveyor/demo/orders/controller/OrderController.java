package io.konveyor.demo.orders.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.konveyor.demo.orders.model.Order;
import io.konveyor.demo.orders.service.OrderService;

@RestController
@RequestMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/{id}")
	public Order getById(@PathVariable("id") Long id) {
		return orderService.findById(id);
	}
	
	@GetMapping
	public List<Order> findAll(Pageable pageable){
		return orderService.findAll(pageable).toList();
	}
}
