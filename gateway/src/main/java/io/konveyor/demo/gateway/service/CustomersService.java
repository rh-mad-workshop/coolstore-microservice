package io.konveyor.demo.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.repository.CustomerRepository;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomersService {
	@Autowired
	private CustomerRepository customerRepository;

	@NewSpan
	public Page<Customer> findAll(@SpanTag Pageable pageable) {
		log.debug("Entering OrdersService.findAll()");

		var orders = customerRepository.findAll(pageable);
		return new PageImpl<Customer>(orders, pageable, orders.size());
	}

	@NewSpan
	public Customer getById(@SpanTag Long id) {
		log.debug("Entering CustomersService.getById()");
		return customerRepository.getCustomerById(id);
	}
}
