package com.redhat.coolstore.inventory.service;


import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;
import com.redhat.coolstore.inventory.model.Product;
import com.redhat.coolstore.inventory.repository.ProductRepository;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@Transactional
@ApplicationScoped
public class ProductService implements IProductService {
	
	private static Logger logger = Logger.getLogger( ProductService.class.getName() );
	
	@Inject
	ProductRepository repository;
	
	/**
	 * Finds a {@link Product} using its {@code id} as search criteria
	 * @param id The {@link Product} {@code id}
	 * @return The {@link Product} with the supplied {@code id}, {@literal null} if no {@link Product} is found. 
	 */
	public Product findById(Long id) {
		logger.debug("Entering ProductService.findById()");
		Product p = repository.findById(id);
		return p;
	}

	@Override
	public List<Product> findAll(Page page, Sort sort) {
		logger.debug("Entering ProductService.findAll()");
		List<Product> p = repository.findAll(page, sort);
		return p;
	}
	
	
}
