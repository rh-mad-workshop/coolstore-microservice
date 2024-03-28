package com.redhat.coolstore.inventory.controller;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.junit.QuarkusTest;

import com.redhat.coolstore.inventory.model.Product;
import io.restassured.http.ContentType;

@QuarkusTest
class BlackBoxTests {
    private static final Product PRODUCT1 = new Product(1L, "Grog", "A secret mixture that contains one or more of the following: Kerosene, Propylene Glycol, Artificial Sweeteners, Sulfuric Acid, Rum, Acetone, Battery Acid, red dye#2, Scumm, Axle grease and/or pepperoni.");
    private static final Product PRODUCT2 = new Product(2L, "Ship's Horn", "Made in Hong Kong");
    private static final Product PRODUCT3 = new Product(3L, "Well-Polished Old Saw", "Found at the bottom of the sea. Great condition.");
    private static final Product PRODUCT4 = new Product(4L, "Rubber Chicken With A Pulley In The Middle", "What possible use could this have?");
    private static final Product PRODUCT5 = new Product(5L, "Idol of Many Hands", "Also known as the Fabulous Idol");
    private static final Product PRODUCT6 = new Product(6L, "How Much Wood? - Hardcover", "From the Woodchuck Mystery series");
    private static final List<Product> ALL_PRODUCTS = List.of(PRODUCT1, PRODUCT2, PRODUCT3, PRODUCT4, PRODUCT5, PRODUCT6);

    @Test
    void findAll() {
        var products = get("/products").then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .jsonPath().getList(".", Product.class);

        assertThat(products)
            .isNotNull()
            .hasSize(6)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(ALL_PRODUCTS);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4, 5, 6 })
    void getById(int id) {
        var product = get("/products/{id}", id).then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract().as(Product.class);

        assertThat(product)
            .isNotNull()
            .usingRecursiveComparison()
            .isEqualTo(ALL_PRODUCTS.get(id - 1));
    }

    @Test
    void getByIdNotFound() {
        get("/products/{id}", 0).then()
            .statusCode(Status.NO_CONTENT.getStatusCode());
    }
}