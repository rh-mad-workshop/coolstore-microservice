package io.konveyor.demo.gateway.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.gateway.model.Order;
import io.konveyor.demo.gateway.model.OrderItem;
import io.konveyor.demo.gateway.model.Product;
import io.konveyor.demo.gateway.service.OrdersService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrdersControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrdersService service;
	
	private Order order;
	
	private OrderItem item;
	
	private Product product;
	
	private Customer customer;
	
	@BeforeEach
	void setUp() {
		order = new Order();
		order.setId(1L);
		order.setDate(new GregorianCalendar(2018, 4, 30).getTime());
		
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
	void getByIdExisting() throws Exception {
		Mockito.when(service.getById(1L))
	      .thenReturn(order);

		this.mockMvc.perform(get("/orders/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.date").value("30-05-2018"))
			.andExpect(jsonPath("$.customer.id").value(1L))
			.andExpect(jsonPath("$.customer.name").value("Test Customer"))
			.andExpect(jsonPath("$.customer.surname").value("Test Customer"))
			.andExpect(jsonPath("$.customer.username").value("testcustomer"))
			.andExpect(jsonPath("$.customer.zipCode").value("28080"))
			.andExpect(jsonPath("$.customer.city").value("Madrid"))
			.andExpect(jsonPath("$.customer.country").value("Spain"))
			.andExpect(jsonPath("$.customer.address").value("Test Address"))
			.andExpect(jsonPath("$.items.size()").value(1))
			.andExpect(jsonPath("$.items[0].quantity").value(3))
			.andExpect(jsonPath("$.items[0].price").value(30))
			.andExpect(jsonPath("$.items[0].product.id").value(1L))
			.andExpect(jsonPath("$.items[0].product.name").value("Test Product"))
			.andExpect(jsonPath("$.items[0].product.description").value("Test Description"));

		Mockito.verify(service).getById(1L);
		Mockito.verifyNoMoreInteractions(service);
	}
	
	@Test
	void getByIdNonExisting() throws Exception {
		Mockito.when(service.getById(1L))
	      .thenReturn(null);

		this.mockMvc.perform(get("/orders/{id}", 1L))
			.andExpect(status().isNotFound());

		Mockito.verify(service).getById(1L);
		Mockito.verifyNoMoreInteractions(service);
	}
}
