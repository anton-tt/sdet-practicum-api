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
import java.util.Set;
import java.util.stream.Collectors;

import static config.ApiRoutes.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API тесты")
@Feature("Работа с сущностью")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityApiTest extends BaseTest {

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

        Integer entityId = Allure.step("Отправка POST " + CREATE, () ->
                createEntity(request)
        );
        Allure.step("Проверка ответа POST", () -> {
            assertNotNull(entityId);
            assertTrue(entityId > 0);
        });

        EntityResponse response = Allure.step("Проверка созданной сущности через GET", () ->
                getEntity(entityId)
        );
        Allure.step("Проверка данных созданной сущности", () -> {
            assertEquals(request.getTitle(), response.getTitle());
            assertEquals(request.getVerified(), response.getVerified());
            assertEquals(request.getImportant_numbers(), response.getImportant_numbers());
            assertEquals(request.getAddition().getAdditional_info(), response.getAddition().getAdditional_info());
            assertEquals(request.getAddition().getAdditional_number(), response.getAddition().getAdditional_number());
        });
    }

    @Test
    @DisplayName("GET: получение сущности по id")
    void getEntityTest() {
        EntityRequest request = EntityRequest.builder()
                .title("Заголовок сущности 1")
                .verified(true)
                .important_numbers(List.of(42, 87, 15))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 1")
                        .additional_number(123)
                        .build())
                .build();
        Integer entityId = Allure.step("Подготовка данных через POST", () ->
                createEntity(request)
        );

        EntityResponse response = Allure.step("Отправка GET " + GET + entityId, () ->
                getEntity(entityId)
        );
        Allure.step("Проверка полученной сущности", () -> {
            assertEquals(entityId, response.getId());
            assertEquals(request.getTitle(), response.getTitle());
            assertEquals(request.getVerified(), response.getVerified());
            assertEquals(request.getImportant_numbers(), response.getImportant_numbers());
            assertEquals(request.getAddition().getAdditional_info(), response.getAddition().getAdditional_info());
            assertEquals(request.getAddition().getAdditional_number(), response.getAddition().getAdditional_number());
        });

    }

    @Test
    @DisplayName("GET: получение всех сущностей")
    void getAllEntityTest() {
        EntityRequest firstRequest = EntityRequest.builder()
                .title("Заголовок сущности 1")
                .verified(true)
                .important_numbers(List.of(42, 87, 15))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 1")
                        .additional_number(123)
                        .build())
                .build();
        Integer firstRequestId = Allure.step("Отправка POST " + CREATE, () ->
                createEntity(firstRequest)
        );

        EntityRequest secondRequest = EntityRequest.builder()
                .title("Заголовок сущности 2")
                .verified(false)
                .important_numbers(List.of(15, 42, 87))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 2")
                        .additional_number(321)
                        .build())
                .build();
        Integer secondRequestId = Allure.step("Отправка POST " + CREATE, () ->
                createEntity(secondRequest)
        );

        Response  response = Allure.step("Отправка GET " + GET_ALL, () ->
                given()
                        .queryParam("page", 1)
                        .queryParam("perPage", 100)
                        .when()
                        .get(GET_ALL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
        );
        EntityResponse[] entities = response.jsonPath().getObject("entity", EntityResponse[].class);

        Allure.step("Проверка списка сущностей", () -> {
            assertNotNull(entities);
            assertTrue(entities.length >= 2);
            Set<Integer> actualIds = Arrays.stream(entities)
                    .map(EntityResponse::getId)
                    .collect(Collectors.toSet());
            assertTrue(actualIds.contains(firstRequestId));
            assertTrue(actualIds.contains(secondRequestId));
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
