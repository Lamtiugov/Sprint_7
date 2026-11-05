import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class CourierLoginApiTests extends CommonTestsSteps {

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

    @DisplayName("Существующий курьер может авторизоваться и возвращается его id")
    @Test
    public void existedCourierCanLoginResult200() {
        courierApiTests.createCourier(validCourierJson);
        var response = courierApiTests.loginCourier(validCourierJson);
        courierApiTests.deleteCourier(response.path("id").toString());
        response.then().assertThat().statusCode(200)
                .and()
                .body("id", notNullValue());
    }

    @DisplayName("Для авторизации нужно передать поле login")
    @Test
    public void notAllowLoginWithoutLoginFieldResult400() {
        courierApiTests.createCourier(validCourierJson);
        var id = courierApiTests.loginCourier(validCourierJson).path("id").toString();

        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.LOGIN);
        var response = courierApiTests.loginCourier(courierJson);
        courierApiTests.deleteCourier(id);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Для авторизации нужно передать поле password")
    @Test
    public void notAllowLoginWithoutPasswordFieldResult400() {
        courierApiTests.createCourier(validCourierJson);
        var id = courierApiTests.loginCourier(validCourierJson).path("id").toString();

        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.PASSWORD);
        var response = courierApiTests.loginCourier(courierJson);

        courierApiTests.deleteCourier(id);

        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Система вернёт ошибку, если неправильно указать пароль")
    @Test
    public void notAllowLoginWithWrongPasswordFieldResult404() {
        courierApiTests.createCourier(validCourierJson);
        var id = courierApiTests.loginCourier(validCourierJson).path("id").toString();

        var courierJson = setParamToJason(validCourierJson, CourierJsonFields.PASSWORD, "111111");
        var response = courierApiTests.loginCourier(courierJson);

        courierApiTests.deleteCourier(id);

        response.then().assertThat().statusCode(404);
    }

    @DisplayName("Система вернёт ошибку, если неправильно указать логин")
    @Test
    public void notAllowLoginWithWrongLoginFieldResult404() {
        courierApiTests.createCourier(validCourierJson);
        var id = courierApiTests.loginCourier(validCourierJson).path("id").toString();

        var courierJson = setParamToJason(validCourierJson, CourierJsonFields.LOGIN, "111111");
        var response = courierApiTests.loginCourier(courierJson);

        courierApiTests.deleteCourier(id);

        response.then().assertThat().statusCode(404);
    }

    @DisplayName("Нельзя авторизоваться под несуществующим пользователем")
    @Test
    public void notAllowLoginNotExistedCourierResult404() {
        var courierJson = setParamToJason("{}", CourierJsonFields.LOGIN, "-1");
        courierJson = setParamToJason(courierJson, CourierJsonFields.PASSWORD, "-1");
        courierJson = setParamToJason(courierJson, CourierJsonFields.FIRST_NAME, "-1");

        var response = courierApiTests.loginCourier(courierJson);

        response.then().assertThat().statusCode(404);
    }



    @Test
    public void test() {
        System.out.println(CourierJsonFields.FIRST_NAME.name());
    }
}
