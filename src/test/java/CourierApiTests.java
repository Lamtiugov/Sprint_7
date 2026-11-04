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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@jdk.jfr.Description("Тесты /api/v1/courier")
public class CourierApiTests {

    private String loginOfCourier = "ninjaLamyiugov";
    private String passwordOfCourier = "1234";
    private String firstNameOfCourier = "saske";
    private boolean isCourierExist;


    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    @AfterEach
    public void deleteCourier() {
        if (isCourierExist) {
            var courier = new Courier(loginOfCourier, passwordOfCourier, firstNameOfCourier);
            String id = loginCourier(courier);
            // Удаление курьера по ID
            String deleteResult = deleteCourier(id);
        }
    }

    @DisplayName("Создание курьера с валидными значениями")
    @Test
    public void createCourierResult201() {
        var courier = new Courier(loginOfCourier, passwordOfCourier, firstNameOfCourier);
        var response = createCourier(courier);
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
        this.isCourierExist = true;
    }

    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Test
    public void notAllowToCreateCourierCloneResult409() {
        var courier = new Courier(loginOfCourier, passwordOfCourier, firstNameOfCourier);
        var response = createCourier(courier);
        response = createCourier(courier);
        this.isCourierExist = true;
        response.then().assertThat().statusCode(409);
    }

    @DisplayName("Нельзя создать пользователя без login")
    @Test
    public void notAllowToCreateCourierWithoutLoginResult400()
    {
        var courier = new Courier(null, passwordOfCourier, firstNameOfCourier);
        var response = createCourier(courier);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Нельзя создать пользователя без login")
    @Test
    public void notAllowToCreateCourierWithoutPasswordResult400()
    {
        var courier = new Courier(loginOfCourier, null, firstNameOfCourier);
        var response = createCourier(courier);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Ошибка: можно создать пользователя без First Name")
    @Test
    public void allowToCreateCourierWithoutFirstNameResult201()
    {
        var courier = new Courier(loginOfCourier, passwordOfCourier, null);
        var response = createCourier(courier);
        response.then().assertThat().statusCode(201);
        isCourierExist = true;
    }

    @DisplayName("Нельзя создать пользователя с логином, который уже есть")
    @Test
    public void notAllowToCreateWithExistedLoginResult409() {
        var courier = new Courier(loginOfCourier, passwordOfCourier, firstNameOfCourier);
        createCourier(courier);
        isCourierExist = true;
        courier = new Courier(loginOfCourier, "11111", "Крокодил Гена");

        var response = createCourier(courier);
        System.out.println(response.statusCode());
        System.out.println(response.asPrettyString());
        response.then().assertThat().statusCode(409);
    }

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(courier.toString())
                        .when()
                        .post();
        System.out.println(response.body().asString());
        return response;
    }

    @Step("Логин курьера в системе с существующим Логином и правильным паролем")
    public String loginCourier(Courier courier) {
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body('{' + courier.getLogin() + ',' + courier.getPassword() + '}')
                        .when()
                        .post("login");
        System.out.println(response.body().asString());
        response.then().assertThat().statusCode(200);
        return response.path("id").toString();
    }

    @Step("Удалить курьера c существующим ID")
    String deleteCourier(String id) {
        var response =
                given()
                        //.queryParam(":id", "14395")
                        .when()
                        .delete(id);
        System.out.println(response.body().asString());
        response.then().assertThat().statusCode(200);
        return response.body().asString();
    }
}
