import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.Courier;

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
            String id = loginCourier(validCourierJson).path("id").toString();
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
        response = createCourier(validCourierJson);
        isCourierExist = true;
        response.then().assertThat().statusCode(409);
    }

    @DisplayName("Нельзя создать пользователя без login")
    @Test
    public void notAllowToCreateCourierWithoutLoginResult400()
    {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validCourierJson), JsonObject.class);
        jsonObject.remove("login");
        var response = createCourier(jsonObject.toString());
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Нельзя создать пользователя без password")
    @Test
    public void notAllowToCreateCourierWithoutPasswordResult400()
    {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validCourierJson), JsonObject.class);
        jsonObject.remove("password");
        var response = createCourier(jsonObject.toString());
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Ошибка: можно создать пользователя без First Name")
    @Test
    public void allowToCreateCourierWithoutFirstNameResult201()
    {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validCourierJson), JsonObject.class);
        jsonObject.remove("firstName");
        var response = createCourier(jsonObject.toString());
        response.then().assertThat().statusCode(201);
        isCourierExist = true;
    }

    @DisplayName("Нельзя создать пользователя с логином, который уже есть")
    @Test
    public void notAllowToCreateWithExistedLoginResult409() {
        createCourier(validCourierJson);
        isCourierExist = true;
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validCourierJson), JsonObject.class);
        jsonObject.addProperty("password", "11111");
        jsonObject.addProperty("firstName", "11111");
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
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(courierJson), JsonObject.class);
        jsonObject.remove("firstName");
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(jsonObject.toString())
                        .when()
                        .post("login");
        System.out.println(response.body().asString());
        response.then().assertThat().statusCode(200);
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

    public String getValidCourierJson() {
        return validCourierJson;
    }

    public boolean isCourierExist() {
        return isCourierExist;
    }
}
