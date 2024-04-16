package io.konveyor.demo.gateway.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.konveyor.demo.gateway.model.Order;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OrderRepository extends GenericRepository {
	private final RestClient restClient;

	public OrderRepository(RestClient.Builder restClientBuilder, @Value("${services.orders.url}") String ordersServiceURL) {
		this.restClient = restClientBuilder.baseUrl(ordersServiceURL)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@CircuitBreaker(name = "Orders", fallbackMethod = "getFallbackOrder")
	@Retry(name = "Orders", fallbackMethod = "getFallbackOrder")
//	@NewSpan
	public Order getOrderById(Long id) {
		log.debug("Entering OrderRepository.getOrderById()");

		var o = this.restClient.get()
			.uri("/{id}", id)
			.retrieve()
			.body(Order.class);

		if (log.isDebugEnabled()) {
			if (o == null) {
				log.debug("Obtained null order");
			}
			else {
				log.debug(o.toString());
			}
		}

		return o;
	}
	
	@CircuitBreaker(name = "AllOrders", fallbackMethod = "getFallbackOrders")
	@Retry(name = "AllOrders", fallbackMethod = "getFallbackOrders")
//	@NewSpan
	public List<Order> findAll(Pageable pageable) {
		log.debug("Entering OrderRepository.findAll()");

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
	

	public Order getFallbackOrder(Long id, Exception e) {
		log.warn("Failed to obtain Order, " + e.getMessage() + " for order with id " + id);
		return null;
	}
	
	public List<Order> getFallbackOrders(Pageable pageable, Exception e) {
		log.warn("Failed to obtain Orders, " + e.getMessage());
		return new ArrayList<>();
	}
	
}
