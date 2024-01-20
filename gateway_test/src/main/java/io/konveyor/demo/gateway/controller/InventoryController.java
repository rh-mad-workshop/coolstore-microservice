package io.konveyor.demo.gateway.controller;

import io.konveyor.demo.gateway.model.Product;
import io.konveyor.demo.gateway.service.InventoryService;
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
@RequestMapping("/products")
@Component
public class InventoryController {
	
	@Autowired
	private InventoryService inventoryService;
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Product getById(@PathVariable("id") Long id) {
		return inventoryService.getById(id);
	}
	
	@RequestMapping
	public Page<Product> findAll(Pageable pageable){
		return inventoryService.findAll(pageable);
	}

}
