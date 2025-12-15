package com.bpce.lab.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class HelloResourceTest {

    @Test
    void shouldReturnHello() {
        given()
          .when().get("/api/hello")
          .then()
             .statusCode(200)
             .body(containsString("hello from quarkus"));
    }
}
