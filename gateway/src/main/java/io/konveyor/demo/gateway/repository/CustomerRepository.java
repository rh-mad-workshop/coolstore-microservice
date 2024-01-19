package io.konveyor.demo.gateway.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.konveyor.demo.gateway.model.Customer;
import io.konveyor.demo.util.PaginatedResponse;

@Component
public class CustomerRepository extends GenericRepository{
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${services.customers.url}")
	String customersServiceURL;
	
	// @HystrixCommand(commandKey = "Customers", fallbackMethod = "getFallbackCustomer", commandProperties = {
    //         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	// })
	public Customer getCustomerById(Long id) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(customersServiceURL)
				.pathSegment( "{customer}");		
		Customer c = restTemplate.getForObject(
				builder.buildAndExpand(id).toUriString(), 
				Customer.class);
		//Trigger fallback if no result is obtained.
		if (c == null) {
			throw new RuntimeException();
		}
		return c;
	}
	
	// @HystrixCommand(commandKey = "AllCustomers", fallbackMethod = "getFallbackCustomers", commandProperties = {
    //         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	// })
	public List<Customer> findAll(Pageable pageable) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(customersServiceURL)
				.queryParam("page", pageable.getPageNumber())
				.queryParam("size", pageable.getPageSize())
				.queryParam("sort", getSortString(pageable));
		ResponseEntity<PaginatedResponse<Customer>> responseEntity = 
				  restTemplate.exchange(
						  builder.toUriString(),
						  HttpMethod.GET,
						  null,
						  new ParameterizedTypeReference<PaginatedResponse<Customer>>() {}
				  );
		List<Customer> customers = responseEntity.getBody().getContent();
		return customers;
	}
	
	public Customer getFallbackCustomer(Long id, Throwable e) {
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
	
	public List<Customer> getFallbackCustomers(Pageable pageable, Throwable e) {
		return new ArrayList<Customer>();
	}

}
