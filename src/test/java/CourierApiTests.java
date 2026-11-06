import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

@jdk.jfr.Description("Тесты /api/v1/courier")
public class CourierApiTests extends CommonTestsSteps {

    private boolean isCourierExist;

    public CourierApiTests() throws IOException {}

    @AfterEach
    public void deleteExistedCourier() {
        if (isCourierExist) {
            var id = loginCourier(validCourierJson).path("id").toString();
            deleteCourier(id);
        }
    }

    @DisplayName("Создание курьера с валидными значениями")
    @Test
    public void createCourierResult201() {
        createCourier(validCourierJson).then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
        isCourierExist = true;
    }

    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Test
    public void notAllowToCreateCourierCloneResult409() {
        createCourier(validCourierJson);
        isCourierExist = true;
        createCourier(validCourierJson).then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @DisplayName("Нельзя создать пользователя без login")
    @Test
    public void notAllowToCreateCourierWithoutLoginResult400() {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.LOGIN.getValue());
        createCourier(courierJson).then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Нельзя создать пользователя без password")
    @Test
    public void notAllowToCreateCourierWithoutPasswordResult400() {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.PASSWORD.getValue());
        createCourier(courierJson).then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Ошибка: можно создать пользователя без First Name")
    @Test
    public void allowToCreateCourierWithoutFirstNameResult201() {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.FIRST_NAME.getValue());
        createCourier(courierJson).then().assertThat().statusCode(201);
        isCourierExist = true;
    }

    @DisplayName("Нельзя создать пользователя с логином, который уже есть")
    @Test
    public void notAllowToCreateWithExistedLoginResult409() {
        createCourier(validCourierJson);
        isCourierExist = true;
        var courierJson = setParamToJson(validCourierJson, CourierJsonFields.PASSWORD.getValue(), "0");
        courierJson = setParamToJson(validCourierJson, CourierJsonFields.FIRST_NAME.getValue(), "0");
        createCourier(courierJson).then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
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
        var response = deleteCourier("0");
        response.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Курьера с таким id нет"));
    }

    @DisplayName("Удаление с пустым ID выдает 400")
    @Test
    public void deleteCourierWithEmptyIdResult400() {
        var response = deleteCourier("");
        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
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