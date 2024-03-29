package io.konveyor.demo.gateway.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.konveyor.demo.gateway.serialization.ProductDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderItem {
	private Product product;
	private Integer quantity;
	private BigDecimal price;

	@JsonProperty("product")
	public Product getProduct() {
		return this.product;
	}
	
	@JsonProperty("productUID")
	@JsonDeserialize(using = ProductDeserializer.class)
	public void setProduct(Product product) {
		this.product = product;
	}
}
