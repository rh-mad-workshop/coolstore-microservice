package io.konveyor.demo.gateway.model;

import java.util.Date;

import lombok.Data;

@Data
public class OrderSummary {
	private long id;
	private String customerName;
	private Date date;
	private double totalAmmount;

}
