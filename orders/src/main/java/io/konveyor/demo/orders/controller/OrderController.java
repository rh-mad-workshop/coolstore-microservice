package io.konveyor.demo.orders.controller;

import java.util.List;

import io.konveyor.demo.orders.model.Order;
import io.konveyor.demo.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order getById(@PathVariable("id") Long id) {
		return orderService.findById(id);
	}
	
	@RequestMapping
	public List<Order> findAll(Pageable pageable){
		return orderService.findAll(pageable).toList();
	}

}
