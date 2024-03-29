package io.konveyor.demo.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.konveyor.demo.gateway.model.Product;
import io.konveyor.demo.gateway.repository.InventoryRepository;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryService {
	@Autowired
	private InventoryRepository inventoryRepository;

	@NewSpan
	public Page<Product> findAll(@SpanTag Pageable pageable) {
		log.debug("Entering OrdersService.findAll()");

		var orders = inventoryRepository.findAll(pageable);
		return new PageImpl<Product>(orders, pageable, orders.size());
	}

	@NewSpan
	public Product getById(@SpanTag Long id) {
		log.debug("Entering CustomersService.getById()");
		return inventoryRepository.getProductById(id);
	}
}
