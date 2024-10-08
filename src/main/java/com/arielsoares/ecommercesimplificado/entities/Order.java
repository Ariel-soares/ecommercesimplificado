package com.arielsoares.ecommercesimplificado.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.arielsoares.ecommercesimplificado.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order")
public class Order implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
	private Instant moment;

	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
	private User client;

	@OneToMany(cascade = CascadeType.ALL)
	private List<OrderItem> items = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	public Order() {
		this.status = OrderStatus.OPEN;
	}

	public Order(User client) {
		this.moment = Instant.now();
		this.client = client;
		this.status = OrderStatus.OPEN;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getMoment() {
		return moment;
	}

	public void setMoment(Instant moment) {
		this.moment = moment;
	}
	
	public List<OrderItem> getItems() {
		return items;
	}

	public User getClient() {
		return client;
	}

	public void setClient(User client) {
		this.client = client;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Double getTotal() {
		Double total = 0.0;
		for (OrderItem item : items) {
			if(item.getActive() == true)total += item.getSubTotal();
		}
		return total;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Order order))
			return false;
		return Objects.equals(getId(), order.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

}
