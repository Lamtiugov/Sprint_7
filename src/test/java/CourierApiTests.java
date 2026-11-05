import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;
import jdk.jfr.Category;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.Courier;
import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@jdk.jfr.Description("Тесты /api/v1/courier")
public class CourierApiTests {

    private String validCourierJson = Files.readString(Path.of("src/test/resources/ValidCourierParams.json"), StandardCharsets.UTF_8);
    private boolean isCourierExist;

    public CourierApiTests() throws IOException {
    }


    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    @AfterEach
    public void deleteCourier() {
        if (isCourierExist) {
            String courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.FIRST_NAME.getValue());
            String id = loginCourier(courierJson).path("id").toString();
            // Удаление курьера по ID
            String deleteResult = deleteCourier(id);
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
    public void notAllowToCreateCourierWithoutLoginResult400()
    {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.LOGIN.getValue());
        var response = createCourier(courierJson);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Нельзя создать пользователя без password")
    @Test
    public void notAllowToCreateCourierWithoutPasswordResult400()
    {
        var courierJson = removeParamFromJson(validCourierJson, CourierJsonFields.PASSWORD.getValue());
        var response = createCourier(courierJson);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Ошибка: можно создать пользователя без First Name")
    @Test
    public void allowToCreateCourierWithoutFirstNameResult201()
    {
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
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validCourierJson), JsonObject.class);
        jsonObject.addProperty(CourierJsonFields.PASSWORD.getValue(), "11111");
        jsonObject.addProperty(CourierJsonFields.FIRST_NAME.getValue(), "11111");
        var response = createCourier(jsonObject.toString());
        response.then().assertThat().statusCode(409);
    }

    @Step("Создание курьера")
    public Response createCourier(String courierJson) {
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(courierJson)
                        .when()
                        .post();
        System.out.println(response.body().asString());
        return response;
    }

    @Step("Логин курьера в системе с существующим Логином и правильным паролем")
    public Response loginCourier(String courierJson) {
        courierJson = removeParamFromJson(courierJson, CourierJsonFields.FIRST_NAME.getValue());
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(courierJson)
                        .when()
                        .post("login");
        System.out.println(response.body().asString());
        return response;
    }

    @Step("Удалить курьера c существующим ID")
    String deleteCourier(String id) {
        var response =
                given()
                        .when()
                        .delete(id);
        System.out.println(response.body().asString());
        response.then().assertThat().statusCode(200);
        return response.body().asString();
    }

    public String removeParamFromJson(String json, String param) {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        jsonObject.remove(param);
        return jsonObject.toString();
    }

    public String getValidCourierJson() {
        return validCourierJson;
    }

    public boolean isCourierExist() {
        return isCourierExist;
    }
}
