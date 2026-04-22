package tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.AdditionRequest;
import models.EntityRequest;
import models.EntityResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API тесты")
@Feature("Работа с сущностью")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityApiTest extends BaseTest {
    private static Integer entityId;

    @Order(1)
    @Test
    @DisplayName("POST: создание сущности")
    void createEntityTest() {
        EntityRequest request = EntityRequest.builder()
                .title("Заголовок сущности 1")
                .verified(true)
                .important_numbers(List.of(42, 87, 15))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 1")
                        .additional_number(123)
                        .build())
                .build();

        Response response = Allure.step("Отправка POST запроса", () ->
                given()
                        .contentType("application/json")
                        .body(request)
                .when()
                        .post("/api/create")
                .then()
                        .statusCode(200)
                        .extract()
                        .response()
        );
        entityId = Integer.valueOf(response.asString());

        Allure.step("Проверка созданного ID", () -> {
            assertNotNull(entityId);
            assertTrue(entityId > 0);
        });
    }

    @Order(2)
    @Test
    @DisplayName("PATCH: обновление сущности")
    void patchEntityTest() {
        EntityRequest request = EntityRequest.builder()
                .title("Заголовок сущности 2")
                .verified(false)
                .important_numbers(List.of(15, 42, 87))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 2")
                        .additional_number(321)
                        .build())
                .build();

        Allure.step("Отправка PATCH запроса", () ->
                given().log().all()
                        .contentType("application/json")
                        .body(request)
                .when()
                        .patch("/api/patch/" + entityId)
                .then()
                        .statusCode(204)
        );

        Response response = Allure.step("GET запрос на получение обновлённой сущности", () ->
                given()
                .when()
                        .get("/api/get/" + entityId)
                .then()
                        .statusCode(200)
                        .extract()
                        .response()
        );
        EntityResponse actual = response.as(EntityResponse.class);

        Allure.step("Проверка обновлённых данных", () -> {
            assertEquals("Заголовок сущности 2", actual.getTitle());
            assertEquals(false, actual.getVerified());
        });
    }

}
