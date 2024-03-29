package io.konveyor.demo.gateway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.math.BigDecimal;
import java.util.List;

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
import io.konveyor.demo.gateway.model.OrderItem;
import io.konveyor.demo.gateway.model.Product;

@RestClientTest(
	components = InventoryRepository.class,
	properties = "services.inventory.url=/products"
)
@ImportAutoConfiguration({ AopAutoConfiguration.class, Resilience4JAutoConfiguration.class, RetryAutoConfiguration.class, CircuitBreakerAutoConfiguration.class, CircuitBreakerMetricsAutoConfiguration.class, TimeLimiterAutoConfiguration.class })
@AutoConfigureObservability
class InventoryRepositoryTest {
	@Autowired
	private MockRestServiceServer server;
	
	@Autowired
	private InventoryRepository repository;

	@Autowired
	private ObjectMapper objectMapper;
	
	private List<OrderItem> items;
	
	private Product product1;
	
	private Product product2;
	
	@BeforeEach
	void setup() {
		product1 = new Product();
		product1.setId(1L);
		product1.setName("Test Product 1");
		product1.setDescription("Test Description 1");
		
		product2 = new Product();
		product2.setId(2L);
		product2.setName("Test Product 2");
		product2.setDescription("Test Description 2");
		
		OrderItem item1 = new OrderItem();
		item1.setPrice(new BigDecimal(30));
		item1.setQuantity(3);
		item1.setProduct(new Product(product1.getId(), product1.getName(), product1.getDescription()));
		
		OrderItem item2 = new OrderItem();
		item2.setPrice(new BigDecimal(50));
		item2.setQuantity(2);
		item2.setProduct(new Product(product2.getId(), product2.getName(), product2.getDescription()));
		
		items = List.of(item1, item2);

		this.server.reset();
	}
	
	@Test
	void getProductDetailsExistingTest() throws JsonProcessingException {
		Product p1 = new Product();
		p1.setId(1L);
		
		Product p2 = new Product();
		p2.setId(2L);
		
		OrderItem i1 = new OrderItem();
		i1.setPrice(new BigDecimal(30));
		i1.setQuantity(3);
		i1.setProduct(p1);
		
		OrderItem i2 = new OrderItem();
		i2.setPrice(new BigDecimal(50));
		i2.setQuantity(2);
		i2.setProduct(p2);
		
		List<OrderItem> incomplete = List.of(i1, i2);

		this.server.expect(requestTo("/products/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(objectMapper.writeValueAsString(product1), MediaType.APPLICATION_JSON));

		this.server.expect(requestTo("/products/2"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(objectMapper.writeValueAsString(product2), MediaType.APPLICATION_JSON));

		var found = repository.getProductDetails(incomplete);
		
		assertThat(found)
			.usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(items);

		this.server.verify();
	}
	
	@Test
	void getProductDetailsOneNonExistingTest() throws JsonProcessingException {
		Product p1 = new Product();
		p1.setId(1L);
		
		Product p2 = new Product();
		p2.setId(2L);
		
		OrderItem i1 = new OrderItem();
		i1.setPrice(new BigDecimal(30));
		i1.setQuantity(3);
		i1.setProduct(p1);
		
		OrderItem i2 = new OrderItem();
		i2.setPrice(new BigDecimal(50));
		i2.setQuantity(2);
		i2.setProduct(p2);
		
		List<OrderItem> incomplete = List.of(i1, i2);

		server.expect(requestTo("/products/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess());

		server.expect(requestTo("/products/2"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(objectMapper.writeValueAsString(product2), MediaType.APPLICATION_JSON));

		var found = repository.getProductDetails(incomplete);
		
		items.get(0).setProduct(p1);

		assertThat(found)
			.usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(items);

		server.verify();
	}
	
	@Test
	void getProductDetailsRestExceptionTest() {
		Product p1 = new Product();
		p1.setId(1L);
		
		Product p2 = new Product();
		p2.setId(2L);
		
		OrderItem i1 = new OrderItem();
		i1.setPrice(new BigDecimal(30));
		i1.setQuantity(3);
		i1.setProduct(p1);
		
		OrderItem i2 = new OrderItem();
		i2.setPrice(new BigDecimal(50));
		i2.setQuantity(2);
		i2.setProduct(p2);
		
		List<OrderItem> incomplete = List.of(i1, i2);

		server.expect(requestTo("/products/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withServerError());

		server.expect(requestTo("/products/2"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withServerError());

		List<OrderItem> found = repository.getProductDetails(incomplete);
		
		assertThat(found)
			.usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(incomplete);

		server.verify();
	}
}
