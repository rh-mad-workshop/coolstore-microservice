package io.konveyor.demo.gateway.controller;

import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.service.CustomersService;
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
@RequestMapping("/customers")
@Component
public class CustomersController {
	
	@Autowired
	private CustomersService customerService;
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Customer getById(@PathVariable("id") Long id) {
		return customerService.getById(id);
	}
	
	@RequestMapping
	public Page<Customer> findAll(Pageable pageable){
		return customerService.findAll(pageable);
	}	
}
