package com.bpce.lab.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

@QuarkusTest
class ToolsResourceTest {

    @Test
    void shouldReturnInfo() {
        given()
          .when().get("/api/tools/info")
          .then()
             .statusCode(200)
             .body("$", hasKey("service"))
             .body("$", hasKey("timestamp"));
    }
}
