package io.konveyor.demo.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.konveyor.demo.orders.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
