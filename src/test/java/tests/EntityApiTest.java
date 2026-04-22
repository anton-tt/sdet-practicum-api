package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.AdditionRequest;
import models.EntityRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("API тесты")
public class EntityApiTest extends BaseTest {
    private static Integer entityId;

    @Test
    void createEntityTest() {
        EntityRequest request = EntityRequest.builder()
                .title("Заголовок сущности")
                .verified(true)
                .important_numbers(List.of(42, 87, 15))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения")
                        .additional_number(123)
                        .build())
                .build();

        Response response =
                given()
                        .contentType("application/json")
                        .body(request)
                .when()
                        .post("/api/create")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();
        entityId = Integer.valueOf(response.asString());

        assertNotNull(entityId);
        assertTrue(entityId > 0);
    }

}
