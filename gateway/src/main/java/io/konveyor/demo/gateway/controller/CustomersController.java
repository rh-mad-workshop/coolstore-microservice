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
import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.service.CustomersService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CustomersController {
	@Autowired
	private CustomersService customerService;

	@GetMapping("/{id}")
	public Customer getById(@PathVariable("id") Long id) {
		log.debug("Entering CustomerController.getById()");
		var c = customerService.getById(id);

		if (c == null) {
			throw new ResourceNotFoundException("Requested customer doesn't exist");
		}

		log.debug("Returning element: " + c);
		return c;
	}
	
	@GetMapping
	public Page<Customer> findAll(Pageable pageable){
		return customerService.findAll(pageable);
	}	
}
