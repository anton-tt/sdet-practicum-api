package tests;

import config.TestConfig;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import models.AdditionRequest;
import models.EntityRequest;
import models.EntityResponse;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static config.ApiRoutes.CREATE;
import static config.ApiRoutes.GET;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTest {

    @BeforeAll
    static void setup() {
        TestConfig config = ConfigFactory.create(TestConfig.class);
        RestAssured.baseURI = config.baseUrl();
    }

    @Step("Подготовка данных для создания стандартной сущности")
    protected EntityRequest buildDefaultEntity() {
        return EntityRequest.builder()
                .title("Заголовок сущности 1")
                .verified(true)
                .important_numbers(List.of(42, 87, 15))
                .addition(AdditionRequest.builder()
                        .additional_info("Дополнительные сведения 1")
                        .additional_number(123)
                        .build())
                .build();
    }

    @Step("Подготовка и отправка запроса для создания сущности")
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

    @Step("Создание стандартной тестовой сущности")
    protected Integer createDefaultEntity() {
        EntityRequest request = buildDefaultEntity();
        return createEntity(request);
    }

    @Step("Подготовка запроса для получения сущности по id = {id}")
    protected EntityResponse getEntity(Integer id) {
        return given()
                .when()
                .get(GET + id)
                .then()
                .statusCode(200)
                .extract()
                .as(EntityResponse.class);
    }

    @Step("Проверка соответствия данных сущности")
    protected void assertEntityEquals(EntityRequest expected, EntityResponse actual) {
        assertAll("Сравнение полей сущности",
                () -> assertEquals(expected.getTitle(), actual.getTitle(), "Некорректный title"),
                () -> assertEquals(expected.getVerified(), actual.getVerified(), "Некорректный verified"),
                () -> assertEquals(expected.getImportant_numbers(), actual.getImportant_numbers(),
                        "Некорректный important_numbers"),
                () -> assertEquals(expected.getAddition().getAdditional_info(), actual.getAddition().getAdditional_info(),
                        "Некорректный additional_info"),
                () -> assertEquals(expected.getAddition().getAdditional_number(), actual.getAddition().getAdditional_number(),
                        "Некорректный additional_number")
        );
    }

}
