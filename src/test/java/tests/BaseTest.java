package tests;

import config.TestConfig;
import io.restassured.RestAssured;
import models.EntityRequest;
import models.EntityResponse;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

import static config.ApiRoutes.CREATE;
import static config.ApiRoutes.GET;
import static config.ApiRoutes.DELETE;
import static io.restassured.RestAssured.given;

public class BaseTest {

    @BeforeAll
    static void setup() {
        TestConfig config = ConfigFactory.create(TestConfig.class);
        RestAssured.baseURI = config.baseUrl();
    }

    protected Integer createEntity(EntityRequest request) {
        return Integer.parseInt(
                given()
                        .contentType("application/json")
                        .body(request)
                        .when()
                        .post(CREATE)
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString()
        );
    }

    protected EntityResponse getEntity(Integer id) {
        return given()
                .when()
                .get(GET + id)
                .then()
                .statusCode(200)
                .extract()
                .as(EntityResponse.class);
    }

}
