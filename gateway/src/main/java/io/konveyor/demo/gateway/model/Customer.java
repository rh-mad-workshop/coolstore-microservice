package io.konveyor.demo.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Customer {
	private Long id;
	private String username;
	private String name;
	private String surname;
	private String address;
	private String zipCode;
	private String city;
	private String country;
}
