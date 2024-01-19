package io.konveyor.demo.gateway.model;

import java.io.Serializable;
import java.util.Date;

public class OrderSummary implements Serializable{
	
	private static final long serialVersionUID = 5290825002214978384L;
	private long id;

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getTotalAmmount() {
		return this.totalAmmount;
	}

	public void setTotalAmmount(double totalAmmount) {
		this.totalAmmount = totalAmmount;
	}
	private String customerName;
	private Date date;
	private double totalAmmount;

}
