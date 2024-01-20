package io.konveyor.demo.gateway.controller;

import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Component
public class OrdersController {
	
	@Autowired
	private OrdersService orderService;
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order getById(@PathVariable("id") Long id) {
		return orderService.getById(id);
	}
	
	@RequestMapping
	public Page<Order> findAll(Pageable pageable){
		return orderService.findAll(pageable);
	}
}
