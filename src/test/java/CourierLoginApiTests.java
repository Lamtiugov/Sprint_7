import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

public class CourierLoginApiTests {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    @DisplayName("Существующий курьер может авторизоваться")
    @Test
    public void existedCourierCanLogin() throws IOException {
        var courierApiTests = new CourierApiTests();
        var validCourierJson = courierApiTests.getValidCourierJson();
        courierApiTests.createCourier(validCourierJson);

        var response = courierApiTests.loginCourier(validCourierJson);
        response.then().assertThat().statusCode(200);

        courierApiTests.deleteCourier(response.path("id").toString());

    }
}
