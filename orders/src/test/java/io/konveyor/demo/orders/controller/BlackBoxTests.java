package io.konveyor.demo.orders.controller;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import io.konveyor.demo.orders.model.Order;
import io.konveyor.demo.orders.model.OrderItem;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BlackBoxTests {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void findAll() {
        var orders = testRestTemplate.exchange("/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>(){})
            .getBody();

        assertThat(orders)
            .isNotNull()
            .hasSize(4);

        verifyOrder1(getOrderById(orders, 1));
        verifyOrder2(getOrderById(orders, 2));
        verifyOrder3(getOrderById(orders, 3));
        verifyOrder4(getOrderById(orders, 4));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4 })
    void getById(int id) {
        var order = Optional.ofNullable(testRestTemplate.getForObject("/orders/{id}", Order.class, id));

        switch (id) {
            case 1:
                verifyOrder1(order);
                break;

            case 2:
                verifyOrder2(order);
                break;

            case 3:
                verifyOrder3(order);
                break;

            case 4:
                verifyOrder4(order);
                break;
        }
    }

    private static Optional<Order> getOrderById(List<Order> orders, long id) {
        return orders.stream()
            .filter(order -> order.getId() == id)
            .findFirst();
    }

    private static void verifyOrder1(Optional<Order> order) {
        assertThat(order)
            .get()
            .extracting(
                Order::getCustomerUID,
                Order::getDate
            )
            .containsExactly(
                1L,
                "2018-05-30"
            );

        var orderItems = order.map(Order::getItems).orElse(List.of());
        assertThat(orderItems)
            .isNotNull()
            .hasSize(6)
            .map(
                OrderItem::getId,
                OrderItem::getProductUID,
                OrderItem::getQuantity,
                OrderItem::getPrice
            )
            .containsOnly(
                tuple(1L, 4L, 1, BigDecimal.valueOf(30L).setScale(2)),
                tuple(2L, 3L, 1, BigDecimal.valueOf(50L).setScale(2)),
                tuple(3L, 5L, 1, BigDecimal.valueOf(200L).setScale(2)),
                tuple(4L, 1L, 4, BigDecimal.valueOf(5L).setScale(2)),
                tuple(5L, 2L, 1, BigDecimal.valueOf(60L).setScale(2)),
                tuple(6L, 6L, 1, BigDecimal.valueOf(20L).setScale(2))
            );
    }

    private static void verifyOrder2(Optional<Order> order) {
        assertThat(order)
            .get()
            .extracting(
                Order::getCustomerUID,
                Order::getDate
            )
            .containsExactly(
                2L,
                "2018-04-12"
            );

        var orderItems = order.map(Order::getItems).orElse(List.of());
        assertThat(orderItems)
            .isNotNull()
            .hasSize(2)
            .map(
                OrderItem::getId,
                OrderItem::getProductUID,
                OrderItem::getQuantity,
                OrderItem::getPrice
            )
            .containsOnly(
                tuple(7L, 3L, 1, BigDecimal.valueOf(45L).setScale(2)),
                tuple(8L, 6L, 1, BigDecimal.valueOf(20L).setScale(2))
            );
    }

    private static void verifyOrder3(Optional<Order> order) {
        assertThat(order)
            .get()
            .extracting(
                Order::getCustomerUID,
                Order::getDate
            )
            .containsExactly(
                5L,
                "2018-04-14"
            );

        var orderItems = order.map(Order::getItems).orElse(List.of());
        assertThat(orderItems)
            .isNotNull()
            .singleElement()
            .extracting(
                OrderItem::getId,
                OrderItem::getProductUID,
                OrderItem::getQuantity,
                OrderItem::getPrice
            )
            .containsExactly(9L, 1L, 5, BigDecimal.valueOf(5L).setScale(2));
    }

    private static void verifyOrder4(Optional<Order> order) {
        assertThat(order)
            .get()
            .extracting(
                Order::getCustomerUID,
                Order::getDate
            )
            .containsExactly(
                4L,
                "2018-04-25"
            );

        var orderItems = order.map(Order::getItems).orElse(List.of());
        assertThat(orderItems)
            .isNotNull()
            .singleElement()
            .extracting(
                OrderItem::getId,
                OrderItem::getProductUID,
                OrderItem::getQuantity,
                OrderItem::getPrice
            )
            .containsExactly(10L, 2L, 1, BigDecimal.valueOf(60L).setScale(2));
    }
}