package io.konveyor.demo.gateway.service;

import java.util.List;

import io.konveyor.demo.gateway.model.Product;
import io.konveyor.demo.gateway.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	public Page<Product> findAll(Pageable pageable) {
		List<Product> orders = inventoryRepository.findAll(pageable);
		return new PageImpl<Product>(orders, pageable, orders.size());
	}

	public Product getById(Long id) {
		return inventoryRepository.getProductById(id);
	}

}
