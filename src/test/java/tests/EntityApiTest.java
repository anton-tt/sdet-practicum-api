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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API тесты")
@Feature("Работа с сущностью")
public class EntityApiTest extends BaseTest {

    @Test
    @DisplayName("POST: создание сущности")
    void createEntityTest() {
        EntityRequest request = buildDefaultEntity();
        Integer entityId = createEntity(request);
        Allure.step("Проверка ответа POST", () -> {
            assertNotNull(entityId);
            assertTrue(entityId > 0);
        });

        EntityResponse response = Allure.step("Проверка созданной сущности через GET", () ->
                getEntity(entityId)
        );
        Allure.step("Проверка данных созданной сущности", () -> {
            assertEntityEquals(request, response);
        });
    }

    @Test
    @DisplayName("GET: получение сущности по id")
    void getEntityTest() {
        EntityRequest request = buildDefaultEntity();
        Integer entityId = createEntity(request);

        EntityResponse response = Allure.step("Отправка GET " + GET + entityId, () ->
                getEntity(entityId)
        );
        Allure.step("Проверка полученной сущности", () -> {
            assertEquals(entityId, response.getId());
            assertEntityEquals(request, response);
        });

    }

    @Test
    @DisplayName("GET: получение всех сущностей")
    void getAllEntityTest() {
        Integer firstRequestId = createDefaultEntity();

        EntityRequest secondRequest = EntityRequest.builder()
                .title("Заголовок сущности 2")
                .verified(false)
                .important_numbers(List.of(15, 42, 87))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 2")
                        .additional_number(321)
                        .build())
                .build();
        Integer secondRequestId = createEntity(secondRequest);

        Response response = Allure.step("Отправка GET " + GET_ALL, () ->
                given()
                        .queryParam("page", 1)
                        .queryParam("perPage", 200)
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

    @Test
    @DisplayName("PATCH: обновление сущности")
    void patchEntityTest() {
        Integer entityId = createDefaultEntity();

        EntityRequest patchRequest = EntityRequest.builder()
                .title("Заголовок сущности 2")
                .verified(true)
                .important_numbers(List.of(42, 87, 15))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 1")
                        .additional_number(123)
                        .build())
                .build();

        Allure.step("Отправка PATCH " + PATCH + entityId, () ->
                given()
                        .contentType("application/json")
                        .body(patchRequest)
                        .when()
                        .patch(PATCH + entityId)
                        .then()
                        .statusCode(204)
        );
        EntityResponse updatedData = Allure.step("Получение обновлённой сущности", () ->
                getEntity(entityId)
        );

        Allure.step("Проверка обновления данных", () -> {
            assertEntityEquals(patchRequest, updatedData);
        });
    }

    @Test
    @DisplayName("DELETE: удаление сущности")
    void deleteEntityTest() {
        Integer entityId = createDefaultEntity();

        Allure.step("Отправка DELETE " + DELETE + entityId, () ->
                given()
                        .when()
                        .delete(DELETE + entityId)
                        .then()
                        .statusCode(204)
        );

        Allure.step("Проверка, что сущность удалена", () ->
                given()
                        .when()
                        .get(GET + entityId)
                        .then()
                        .statusCode(anyOf(is(400), is(404), is(500)))
        );
    }

}
