import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

public class CourierLoginApiTests extends CommonTestsSteps {

    public CourierLoginApiTests() throws IOException {}

    @DisplayName("Существующий курьер может авторизоваться и возвращается его id")
    @Test
    public void existedCourierCanLoginResult200() {
        createCourier(validCourierJson);
        var response = loginCourier(validCourierJson);
        deleteCourier(response.path("id").toString());
        response.then().assertThat().statusCode(200)
                .and()
                .body("id", notNullValue());
    }

    @DisplayName("Для авторизации нужно передать поле login")
    @Test
    public void notAllowLoginWithoutLoginFieldResult400() {
        createCourier(validCourierJson);
        var id = loginCourier(validCourierJson).path("id").toString();

        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.LOGIN.getValue());
        var response = loginCourier(courierJson);

        deleteCourier(id);

        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Для авторизации нужно передать поле password")
    @Test
    public void notAllowLoginWithoutPasswordFieldResult400() {
        createCourier(validCourierJson);
        var id = loginCourier(validCourierJson).path("id").toString();

        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.PASSWORD.getValue());
        var response = loginCourier(courierJson);

        deleteCourier(id);

        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Система вернёт ошибку, если неправильно указать пароль")
    @Test
    public void notAllowLoginWithWrongPasswordFieldResult404() {
        createCourier(validCourierJson);
        var id = loginCourier(validCourierJson).path("id").toString();

        var courierJson = setParamToJson(validCourierJson, CourierJsonFields.PASSWORD.getValue(), "0");
        var response = loginCourier(courierJson);

        deleteCourier(id);

        response.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Система вернёт ошибку, если неправильно указать логин")
    @Test
    public void notAllowLoginWithWrongLoginFieldResult404() {
        createCourier(validCourierJson);
        var id = loginCourier(validCourierJson).path("id").toString();

        var courierJson = setParamToJson(validCourierJson, CourierJsonFields.LOGIN.getValue(), "0");
        var response = loginCourier(courierJson);

        deleteCourier(id);

        response.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Нельзя авторизоваться под несуществующим пользователем")
    @Test
    public void notAllowLoginNotExistedCourierResult404() {
        var courierJson = setParamToJson("{}", CourierJsonFields.LOGIN.getValue(), "0");
        courierJson = setParamToJson(courierJson, CourierJsonFields.PASSWORD.getValue(), "0");
        courierJson = setParamToJson(courierJson, CourierJsonFields.FIRST_NAME.getValue(), "0");

        loginCourier(courierJson).then().assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

}