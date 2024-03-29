package io.konveyor.demo.gateway.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.konveyor.demo.gateway.model.OrderItem;
import io.konveyor.demo.gateway.model.Product;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class InventoryRepository extends GenericRepository {
	private final ApplicationContext applicationContext;
	private final RestClient restClient;

	public InventoryRepository(ApplicationContext applicationContext, RestClient.Builder restClientBuilder, @Value("${services.inventory.url}") String inventoryServiceURL) {
      this.applicationContext = applicationContext;
      this.restClient = restClientBuilder.baseUrl(inventoryServiceURL)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@CircuitBreaker(name = "ProductDetails", fallbackMethod = "getFallbackProduct")
	@Retry(name = "ProductDetails", fallbackMethod = "getFallbackProduct")
	public Product getProduct(OrderItem item) {
		return this.restClient.get()
			.uri("/{id}", item.getProduct().getId())
			.retrieve()
			.body(Product.class);
	}

	@NewSpan
	public List<OrderItem> getProductDetails(@SpanTag List<OrderItem> items) {
		log.debug("Entering InventoryRepository.getProductDetails()");

		return items.stream()
			.map(item -> {
				// Need to do this so that the annotations work
				// If its just a local method call then none of the AOP stuff takes effect
				var product = this.applicationContext.getBean(InventoryRepository.class).getProduct(item);
				var i = item.toBuilder();

				if (product != null) {
					i.product(product);
				}

				return i.build();

			})
			.toList();
	}

	@CircuitBreaker(name = "AllProducts", fallbackMethod = "getFallbackProducts")
	@Retry(name = "AllProducts", fallbackMethod = "getFallbackProducts")
	@NewSpan
	public List<Product> findAll(@SpanTag Pageable pageable) {
		log.debug("Entering InventoryRepository.findAll()");

		return this.restClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("page", pageable.getPageNumber())
				.queryParam("size", pageable.getPageSize())
				.queryParam("sort", getSortString(pageable))
				.build()
			)
			.retrieve()
			.body(new ParameterizedTypeReference<>() {});
	}

	@CircuitBreaker(name = "Products", fallbackMethod = "getFallbackProduct")
	@Retry(name = "Products", fallbackMethod = "getFallbackProduct")
	@NewSpan
	public Product getProductById(@SpanTag Long id) {
		log.debug("Entering InventoryRepository.getProductById()");

		var p = this.restClient.get()
			.uri("/{id}", id)
			.retrieve()
			.body(Product.class);

		//Trigger fallback if no result is obtained.
		if (p == null) {
			throw new RuntimeException();
		}
		log.debug(p.toString());
		return p;
	}

	public List<Product> getFallbackProducts(Pageable pageable, Exception e) {
		log.warn("Failed to obtain Products, " + e.getMessage());
		return new ArrayList<>();
	}

	public Product getFallbackProduct(Long id, Exception e) {
		log.warn("Failed to obtain Product, " + e.getMessage() + " for Product with id " + id);

		return Product.builder()
			.id(id)
			.name("Unknown")
			.description("Unknown")
			.build();
	}

	public Product getFallbackProduct(OrderItem item, Exception e) {
		return Product.builder()
			.id(item.getProduct().getId())
			.build();
	}
}
