package com.redhat.coolstore.inventory.service;

import java.util.List;

import com.redhat.coolstore.inventory.model.Product;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

public interface IProductService {
	
	public Product findById(Long id);
	
	public List<Product> findAll(Page page, Sort sort);
}
