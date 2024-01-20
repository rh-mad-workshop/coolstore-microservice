package io.konveyor.demo.gateway.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import io.konveyor.demo.gateway.serialization.CustomerDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Serializable {

	private static final long serialVersionUID = -1703065930151811237L;
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<OrderItem> getItems() {
		return this.items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	private Customer customer;
	private String date;
	private List<OrderItem> items;
	
	public BigDecimal getTotalAmmount() {
		if (items != null) {
			Function<OrderItem, BigDecimal> totalMapper = item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()));
			return items.stream()
			        .map(totalMapper)
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
		} else {
			return new BigDecimal(0);
		}
	}
	
	@JsonProperty("customer")
	public Customer getCustomer() {
		return this.customer;
	}
	
	@JsonProperty("customerUID")
	@JsonDeserialize(using = CustomerDeserializer.class)
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
