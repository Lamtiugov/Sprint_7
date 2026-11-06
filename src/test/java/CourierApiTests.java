import io.qameta.allure.Step;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@jdk.jfr.Description("Тесты /api/v1/courier")
public class CourierApiTests extends CommonTestsSteps {

    private boolean isCourierExist;

    public CourierApiTests() throws IOException {}

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    @AfterEach
    public void deleteExistedCourier() {
        if (isCourierExist) {
            var id = loginCourier(validCourierJson).path("id").toString();
            var deleteResult = deleteCourier(id);
        }
    }

    @DisplayName("Создание курьера с валидными значениями")
    @Test
    public void createCourierResult201() {
        var response = createCourier(validCourierJson);
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
        isCourierExist = true;
    }

    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Test
    public void notAllowToCreateCourierCloneResult409() {
        var response = createCourier(validCourierJson);
        isCourierExist = true;
        response = createCourier(validCourierJson);
        response.then().assertThat().statusCode(409);
    }

    @DisplayName("Нельзя создать пользователя без login")
    @Test
    public void notAllowToCreateCourierWithoutLoginResult400() {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.LOGIN.getValue());
        var response = createCourier(courierJson);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Нельзя создать пользователя без password")
    @Test
    public void notAllowToCreateCourierWithoutPasswordResult400() {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.PASSWORD.getValue());
        var response = createCourier(courierJson);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Ошибка: можно создать пользователя без First Name")
    @Test
    public void allowToCreateCourierWithoutFirstNameResult201() {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.FIRST_NAME.getValue());
        var response = createCourier(courierJson);
        response.then().assertThat().statusCode(201);
        isCourierExist = true;
    }

    @DisplayName("Нельзя создать пользователя с логином, который уже есть")
    @Test
    public void notAllowToCreateWithExistedLoginResult409() {
        createCourier(validCourierJson);
        isCourierExist = true;
        var courierJson = setParamToJson(validCourierJson, CourierJsonFields.PASSWORD.getValue(), "11111");
        courierJson = setParamToJson(validCourierJson, CourierJsonFields.FIRST_NAME.getValue(), "11111");
        var response = createCourier(courierJson);
        response.then().assertThat().statusCode(409);
    }

    @DisplayName("Ошибка. Удаление с пустым ID выдает 400")
    @Test
    public void deleteCourierWithoutID() {
        var response = deleteCourier("");
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Успешное удаление возвращает ok: true")
    @Test
    public void deleteWithExistedIdReturnOkTrue() {
        createCourier(validCourierJson);
        var response = loginCourier(validCourierJson);
        var id = response.path("id").toString();
        response = deleteCourier(id);
        response.then().assertThat().body("ok", equalTo(true));
    }

    @DisplayName("Удаление с несуществующим ID выдает 404")
    @Test
    public void deleteCourierWithNotExistedID() {
        // Конечно тут надо бы ззалогиниться сперва попробовать...
        var response = deleteCourier("-1");
        response.then().assertThat().statusCode(404);
    }

    @DisplayName("Удаление с пустым ID выдает 400")
    @Test
    public void deleteCourierWithEmptyIdResult400() {
        var response = deleteCourier("");
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Удаление по json существующего курьера")
    @Test
    public void deleteExistedCourierByJsonResult200() {
        createCourier(validCourierJson);
        isCourierExist = true;
        var json4Delete = loginCourier(validCourierJson).body().asString();  // Ответ совпадает с json для удаления
        var response = deleteCourierByJson(json4Delete);

        response.then().assertThat().statusCode(200);
    }

}