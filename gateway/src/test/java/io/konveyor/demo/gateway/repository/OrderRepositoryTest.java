package io.konveyor.demo.gateway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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

import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerMetricsAutoConfiguration;
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration;
import io.github.resilience4j.springboot3.timelimiter.autoconfigure.TimeLimiterAutoConfiguration;
import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.model.OrderItem;
import io.konveyor.demo.gateway.model.Product;

@RestClientTest(
	components = OrderRepository.class,
	properties = "services.orders.url=/orders"
)
@ImportAutoConfiguration({ AopAutoConfiguration.class, Resilience4JAutoConfiguration.class, RetryAutoConfiguration.class, CircuitBreakerAutoConfiguration.class, CircuitBreakerMetricsAutoConfiguration.class, TimeLimiterAutoConfiguration.class })
@AutoConfigureObservability
class OrderRepositoryTest {
	@Autowired
	private MockRestServiceServer server;
	
	@Autowired
	private OrderRepository repository;

	private Order order;
	
	@BeforeEach
	void setup() {
		order = new Order();
		order.setId(11L);
		order.setDate(Date.from(LocalDate.of(2018, 04, 30).atStartOfDay(ZoneId.of("CET")).toInstant()));
		
		Product p = new Product();
		p.setId(1L);
		
		Customer c = new Customer();
		c.setId(1L);
		
		OrderItem i = new OrderItem();
		i.setPrice(new BigDecimal(30));
		i.setQuantity(3);
		i.setProduct(p);
		
		List<OrderItem> items = List.of(i);

		order.setItems(items);
		order.setCustomer(c);
	}
	
	@Test
	void getOrderByIdExistingTest() {
		var json = """
			{
			  "id": 11,
			  "date": "30-04-2018",
			  "items": [
			    {
			      "quantity": 3,
			      "price": 30,
			      "productUID": 1
			    }
			  ],
			  "totalAmmount": 90,
			  "customerUID": 1
			}
			""";

		this.server.expect(requestTo("/orders/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

		Order found = repository.getOrderById(1L);
		
		assertThat(found)
			.usingRecursiveComparison()
			.isEqualTo(order);

		this.server.verify();
	}
	
	@Test
	void getOrderByIdNonExistingTest() {
		this.server.expect(requestTo("/orders/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess());

		Order found = repository.getOrderById(1L);
		
		assertThat(found)
			.isNull();
		this.server.verify();
	}
	
	@Test
	void getFallbackCustomerTest() {
		assertThat(repository.getFallbackOrder(1L, new Exception())).isNull();
	}

	@Test
	void fallsBack() {
		var fallbackOrder = repository.getFallbackOrder(1L, new Exception());

		this.server.expect(requestTo("/orders/1"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withServerError());

		assertThat(repository.getOrderById(1L))
			.usingRecursiveComparison()
			.isEqualTo(fallbackOrder);

		this.server.verify();
	}
}
