import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.fail;

public class CourierLoginApiTests {

    private CourierApiTests courierApiTests;
    private String validCourierJson;

    public CourierLoginApiTests() throws IOException {
        courierApiTests = new CourierApiTests();
        validCourierJson = courierApiTests.getValidCourierJson();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    @DisplayName("Существующий курьер может авторизоваться")
    @Test
    public void existedCourierCanLoginResult200() {
        var response = courierApiTests.loginCourier(validCourierJson);
        response.then().assertThat().statusCode(200);

        courierApiTests.deleteCourier(response.path("id").toString());
    }

    @DisplayName("Для авторизации нужно передать поле login")
    @Test
    public void notAllowLoginWithoutLoginFieldResult400() {
        var courierJson = courierApiTests.removeParamFromJson(validCourierJson, CourierJsonFields.LOGIN.getValue());
        var response = courierApiTests.loginCourier(courierJson);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Для авторизации нужно передать поле password")
    @Test
    public void notAllowLoginWithoutPasswordFieldResult400() {
        var courierJson = courierApiTests.removeParamFromJson(validCourierJson, CourierJsonFields.PASSWORD.getValue());
        var response = courierApiTests.loginCourier(courierJson);
        response.then().assertThat().statusCode(400);
    }

    @Test
    public void test() {
        System.out.println(CourierJsonFields.FIRST_NAME.name());
    }
}
