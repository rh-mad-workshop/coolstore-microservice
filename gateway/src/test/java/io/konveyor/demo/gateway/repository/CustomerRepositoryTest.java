package io.konveyor.demo.gateway.repository;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JAutoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerMetricsAutoConfiguration;
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration;
import io.github.resilience4j.springboot3.timelimiter.autoconfigure.TimeLimiterAutoConfiguration;
import io.konveyor.demo.gateway.model.Customer;

@RestClientTest(
	components = CustomerRepository.class,
	properties = "services.customers.url=/customers"
)
@ImportAutoConfiguration({ AopAutoConfiguration.class, Resilience4JAutoConfiguration.class, RetryAutoConfiguration.class, CircuitBreakerAutoConfiguration.class, CircuitBreakerMetricsAutoConfiguration.class, TimeLimiterAutoConfiguration.class })
@AutoConfigureObservability
class CustomerRepositoryTest {
	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	private static Customer CUSTOMER;
	private String customerJson;

	@BeforeAll
	static void createCustomer() {
		CUSTOMER = new Customer();
		CUSTOMER.setId(1L);
		CUSTOMER.setName("Test Customer");
		CUSTOMER.setSurname("Test Customer");
		CUSTOMER.setUsername("testcustomer");
		CUSTOMER.setZipCode("28080");
		CUSTOMER.setCountry("Spain");
		CUSTOMER.setCity("Madrid");
		CUSTOMER.setAddress("Test Address");
	}

	@BeforeEach
	void createCustomerJson() throws JsonProcessingException {
		this.customerJson = this.objectMapper.writeValueAsString(CUSTOMER);
		this.server.reset();
	}

	@Test
	void getCustomerByIdExistingTest() {
		this.server.expect(requestTo("/customers/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(customerJson, MediaType.APPLICATION_JSON));

		var customer = repository.getCustomerById(1L);

		assertThat(customer)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(CUSTOMER);

		this.server.verify();
	}

	@Test
	void getCustomerByIdNonExistingTest() {
		this.server.expect(requestTo("/customers/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess());


		var fallbackCustomer = repository.getFallbackCustomer(1L, new Exception());

		assertThat(repository.getCustomerById(1L))
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(fallbackCustomer);

		this.server.verify();
	}

	@Test
	void fallsBack() {
		var fallbackCustomer = repository.getFallbackCustomer(1L, new Exception());

		this.server.expect(requestTo("/customers/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withServerError());

		assertThat(repository.getCustomerById(1L))
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(fallbackCustomer);

		this.server.verify();
	}

	@Test
	void getFallbackCustomerTest() {
		Customer c = new Customer();
		c.setId(1L);
		c.setUsername("Unknown");
		c.setName("Unknown");
		c.setSurname("Unknown");
		c.setAddress("Unknown");
		c.setCity("Unknown");
		c.setCountry("Unknown");
		c.setZipCode("Unknown");

		var customer = repository.getFallbackCustomer(1L, new Exception());

		assertThat(customer)
			.usingRecursiveComparison()
			.isEqualTo(c);
	}
}
