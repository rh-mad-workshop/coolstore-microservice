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
import io.konveyor.demo.gateway.model.Product;
import io.konveyor.demo.gateway.service.InventoryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;


	@GetMapping("/{id}")
	public Product getById(@PathVariable("id") Long id) {
		log.debug("Entering InventoryController.getById()");
		var p = inventoryService.getById(id);

		if (p == null) {
			throw new ResourceNotFoundException("Requested product doesn't exist");
		}

		log.debug("Returning element: " + p);
		return p;
	}

	@GetMapping
	public Page<Product> findAll(Pageable pageable){
		return inventoryService.findAll(pageable);
	}

}
