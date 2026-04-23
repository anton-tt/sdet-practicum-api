package tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.AdditionRequest;
import models.EntityRequest;
import models.EntityResponse;
import org.junit.jupiter.api.*;

import java.util.Arrays;
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

        Response response = Allure.step("Отправка POST api/create", () ->
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
    @DisplayName("GET: получение сущности по id")
    void getEntityTest() {
        Response response = Allure.step("Отправка GET /api/get/{id}", () ->
                given()
                .when()
                        .get("/api/get/" + entityId)
                .then()
                        .statusCode(200)
                        .extract()
                        .response()
        );
        EntityResponse actual = response.as(EntityResponse.class);

        Allure.step("Проверка полученной сущности", () -> {
            assertEquals(entityId, actual.getId());
            assertEquals("Заголовок сущности 1", actual.getTitle());
            assertTrue(actual.getVerified());

            assertNotNull(actual.getImportant_numbers());
            assertFalse(actual.getImportant_numbers().isEmpty());

            assertNotNull(actual.getAddition());
            assertEquals("Дополнительные сведения 1", actual.getAddition().getAdditional_info());
            assertEquals(123, actual.getAddition().getAdditional_number());
        });
    }

    @Order(3)
    @Test
    @DisplayName("GET: получение всех сущностей")
    void getAllEntityTest() {
        Response response = Allure.step("Отправка GET /api/getAll", () ->
                given()//.log().all()
                        .queryParam("page", 1)
                        .queryParam("perPage", 25)
                .when()
                        .get("/api/getAll")
                .then()
                        .statusCode(200)
                        .extract()
                        .response()
        );
        EntityResponse[] entities = response.jsonPath().getObject("entity", EntityResponse[].class);

        Allure.step("Проверка списка сущностей", () -> {
            assertNotNull(entities);
            assertTrue(entities.length > 0);
            boolean found = Arrays.stream(entities).anyMatch(e -> e.getId().equals(entityId));
            assertTrue(found);
        });
    }

    @Order(4)
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

        Allure.step("Отправка PATCH /api/patch/{id}", () ->
                given()
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

    @Order(5)
    @Test
    @DisplayName("DELETE: удаление сущности")
    void deleteEntityTest() {
        Allure.step("Отправка DELETE /api/delete/{id}", () ->
                given()
                .when()
                        .delete("/api/delete/" + entityId)
                .then()
                        .statusCode(204)
        );
    }

}
