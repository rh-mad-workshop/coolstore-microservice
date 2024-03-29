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
import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.util.PaginatedResponse;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CustomerRepository extends GenericRepository {
	private final RestClient restClient;

	public CustomerRepository(RestClient.Builder restClientBuilder, @Value("${services.customers.url}") String customersServiceURL) {
		this.restClient = restClientBuilder.baseUrl(customersServiceURL)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@CircuitBreaker(name = "Customers", fallbackMethod = "getFallbackCustomer")
	@Retry(name = "Customers", fallbackMethod = "getFallbackCustomer")
	@NewSpan
	public Customer getCustomerById(@SpanTag Long id) {
		log.debug("Entering OrdersService.getCustomerById()");

		var c = this.restClient.get()
			.uri("/{id}", id)
			.retrieve()
			.body(Customer.class);

		//Trigger fallback if no result is obtained.
		if (c == null) {
			throw new RuntimeException();
		}

		log.debug(c.toString());
		return c;
	}

	@CircuitBreaker(name = "AllCustomers", fallbackMethod = "getFallbackCustomers")
	@Retry(name = "AllCustomers", fallbackMethod = "getFallbackCustomer")
	@NewSpan
	public List<Customer> findAll(@SpanTag Pageable pageable) {
		log.debug("Entering CustomerRepository.findAll()");

		return this.restClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("page", pageable.getPageNumber())
				.queryParam("size", pageable.getPageSize())
				.queryParam("sort", getSortString(pageable))
				.build()
			)
			.retrieve()
			.body(new ParameterizedTypeReference<PaginatedResponse<Customer>>() {})
			.getContent();
	}
	
	public Customer getFallbackCustomer(Long id, Exception e) {
		log.warn("Failed to obtain Customer, " + e.getMessage() + " for customer with id " + id);
		Customer c = new Customer();
		c.setId(id);
		c.setUsername("Unknown");
		c.setName("Unknown");
		c.setSurname("Unknown");
		c.setAddress("Unknown");
		c.setCity("Unknown");
		c.setCountry("Unknown");
		c.setZipCode("Unknown");
		return c;
	}
	
	public List<Customer> getFallbackCustomers(Pageable pageable, Exception e) {
		log.warn("Failed to obtain Customers, " + e.getMessage());
		return new ArrayList<>();
	}
}
