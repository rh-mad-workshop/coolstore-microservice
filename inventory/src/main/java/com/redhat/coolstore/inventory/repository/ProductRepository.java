package com.redhat.coolstore.inventory.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import com.redhat.coolstore.inventory.model.Product;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
//	@WithSpan
	public List<Product> findAll(Page page, Sort sort) {
		Log.debug("Entering ProductRepository.findAll()");
		return findAll(sort)
				.page(page)
				.list();
	}
}
