package tests;

import config.TestConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    @BeforeAll
    static void setup() {
        TestConfig config = ConfigFactory.create(TestConfig.class);
        RestAssured.baseURI = config.baseUrl();
    }

}
