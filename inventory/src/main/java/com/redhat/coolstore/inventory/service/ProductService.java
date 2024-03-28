package com.redhat.coolstore.inventory.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import com.redhat.coolstore.inventory.model.Product;
import com.redhat.coolstore.inventory.repository.ProductRepository;

@Transactional
@ApplicationScoped
public class ProductService implements IProductService {
	
	@Inject
	ProductRepository repository;
	
	/**
	 * Finds a {@link Product} using its {@code id} as search criteria
	 *
	 * @param id The {@link Product} {@code id}
	 * @return The {@link Product} with the supplied {@code id}, {@literal null} if no {@link Product} is found.
	 */
	public Product findById(Long id) {
		Log.debug("Entering ProductService.findById()");
		return repository.findById(id);
	}

	@Override
	public List<Product> findAll(Page page, Sort sort) {
		Log.debug("Entering ProductService.findAll()");
		return repository.findAll(page, sort);
	}
	
	
}
