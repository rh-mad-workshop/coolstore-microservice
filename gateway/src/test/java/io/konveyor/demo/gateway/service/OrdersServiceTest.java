package io.konveyor.demo.gateway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.model.OrderItem;
import io.konveyor.demo.gateway.model.Product;
import io.konveyor.demo.gateway.repository.CustomerRepository;
import io.konveyor.demo.gateway.repository.InventoryRepository;
import io.konveyor.demo.gateway.repository.OrderRepository;

@SpringBootTest
class OrdersServiceTest {

	@TestConfiguration
	static class OrdersServiceTestContextConfiguration {
		@Bean
		public OrdersService ordersService() {
			return new OrdersService();
		}
	}

	@Autowired
	private OrdersService ordersService;

	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private InventoryRepository inventoryRepository;

	private Order order;

	private OrderItem item;

	private Product product;

	private Customer customer;

	@BeforeEach
	void setup() {
		order = new Order();
		order.setId(1L);
		order.setDate(new GregorianCalendar(2018, 5, 30).getTime());

		customer = new Customer();
		customer.setId(1L);
		customer.setName("Test Customer");
		customer.setSurname("Test Customer");
		customer.setUsername("testcustomer");
		customer.setZipCode("28080");
		customer.setCountry("Spain");
		customer.setCity("Madrid");
		customer.setAddress("Test Address");

		product = new Product();
		product.setId(1L);
		product.setName("Test Product");
		product.setDescription("Test Description");

		item = new OrderItem();
		item.setPrice(new BigDecimal(30));
		item.setQuantity(3);
		item.setProduct(product);

		List<OrderItem> items = List.of(item);
		order.setItems(items);
		order.setCustomer(customer);
	}

	@Test
	void getByIdWithExistingOrderTest() {
		Order o = new Order();
		o.setId(1L);
		o.setDate(new GregorianCalendar(2018, 5, 30).getTime());

		Product p = new Product();
		p.setId(1L);

		Customer c = new Customer();
		c.setId(1L);

		OrderItem i = new OrderItem();
		i.setPrice(new BigDecimal(30));
		i.setQuantity(3);
		i.setProduct(p);

		List<OrderItem> items = List.of(item);

		List<OrderItem> detaileditems = List.of(new OrderItem(item.getProduct(), item.getQuantity(), item.getPrice()));

		o.setItems(items);
		o.setCustomer(c);

		Mockito.when(orderRepository.getOrderById(1L))
			.thenReturn(o);

		Mockito.when(customerRepository.getCustomerById(1L))
			.thenReturn(customer.toBuilder().build());

		Mockito.when(inventoryRepository.getProductDetails(items))
			.thenReturn(detaileditems);

		Order found = ordersService.getById(1L);

		assertThat(found)
			.usingRecursiveComparison()
			.isEqualTo(order);
	}

	@Test
	void getByIdWithNonExistingOrderTest() {
		Mockito.when(orderRepository.getOrderById(1L))
			.thenReturn(null);

		Order found = ordersService.getById(1L);

		assertThat(found).isNull();
	}
}
