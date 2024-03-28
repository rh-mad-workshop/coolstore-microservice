package com.redhat.coolstore.inventory.repository;

import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import com.redhat.coolstore.inventory.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

	private static Logger logger = Logger.getLogger(ProductRepository.class.getName());

	public Product findById(Long id) {
		logger.debug("Entering ProductRepository.findById()");
		Product p = find("id", id).firstResult();
		return p;
	}

	public List<Product> findAll(Page page, Sort sort) {
		logger.debug("Entering ProductRepository.findAll()");
		List<Product> p = Product.findAll(sort)
				.page(page)
				.list();
		return p;
	}

}
