package io.konveyor.demo.gateway.service;

import java.util.List;

import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomersService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public Page<Customer> findAll(Pageable pageable) {
		List<Customer> orders = customerRepository.findAll(pageable);
		return new PageImpl<Customer>(orders, pageable, orders.size());
	}

	public Customer getById(Long id) {
		Customer c = customerRepository.getCustomerById(id);
		return c;
	}

}
