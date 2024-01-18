package io.konveyor.demo.orders.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {

	private static final long serialVersionUID = 2888227823793439361L;
	@Id
    @SequenceGenerator(
            name = "orderItemSequence",
            sequenceName = "orderitem_id_seq",
            allocationSize = 1,
            initialValue = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderItemSequence")
	private Long id;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
	private Order order;
	
	@Column(name = "product_uid")
	private Long productUID;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Long getProductUID() {
		return this.productUID;
	}

	public void setProductUID(Long productUID) {
		this.productUID = productUID;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	private Integer quantity;
	
	private BigDecimal price;

}
