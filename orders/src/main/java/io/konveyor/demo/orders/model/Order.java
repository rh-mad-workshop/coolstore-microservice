package io.konveyor.demo.orders.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

	private static final long serialVersionUID = -2539346800640467934L;
	
	@Id
    @SequenceGenerator(
            name = "orderSequence",
            sequenceName = "order_id_seq",
            allocationSize = 1,
            initialValue = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderSequence")
	private Long id;
	
	@Column(name = "customer_uid")
	private Long customerUID;
	
	@Column(name = "order_date")
	private String date;
	
	@JsonManagedReference
	@OneToMany(
	        mappedBy = "order", 
	        cascade = CascadeType.ALL, 
	        orphanRemoval = true)
	private List<OrderItem> items;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerUID() {
		return this.customerUID;
	}

	public void setCustomerUID(Long customerUID) {
		this.customerUID = customerUID;
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

}
