package com.redhat.coolstore.inventory.service;

import java.util.List;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import com.redhat.coolstore.inventory.model.Product;

public interface IProductService {
	
	Product findById(Long id);
	
	List<Product> findAll(Page page, Sort sort);
}
