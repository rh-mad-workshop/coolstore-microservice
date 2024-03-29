package io.konveyor.demo.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.konveyor.demo.gateway.exception.ResourceNotFoundException;
import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.service.OrdersService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class OrdersController {
	
	@Autowired
	private OrdersService orderService;

	@GetMapping("/{id}")
	public Order getById(@PathVariable("id") Long id) {
		log.debug("Entering OrderController.getById()");
		var o = orderService.getById(id);

		if (o == null) {
			throw new ResourceNotFoundException("Requested order doesn't exist");
		}

		log.debug("Returning element: " + o);
		return o;
	}

	@GetMapping
	public Page<Order> findAll(Pageable pageable){
		return orderService.findAll(pageable);
	}
}
