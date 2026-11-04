import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.Courier;

import static io.restassured.RestAssured.given;

@jdk.jfr.Description("Тесты /api/v1/courier")
public class CourierApiTests {

    private String loginOfCourier = "ninjaLamyiugov";
    private String passwordOfCourier = "1234";
    private String firstNameOfCourier = "saske";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    // Создание курьера с валидными значениями

    @Description("Тесты /api/v1/courier")
    @DisplayName("Создание курьера с валидными значениями")
    @Test
    public void createCourier() {
        var courier = createCourier(loginOfCourier, passwordOfCourier, firstNameOfCourier);

        // Логин курьера, чтобы получить ID
        // courier = new Courier(loginOfCourier, passwordOfCourier, firstNameOfCourier);
        //System.out.println('{' + courier.getLogin() + ',' + courier.getPassword() + '}');
        String id = loginCourier(courier);
        // Удаление курьера по ID
        String deleteResult = deleteCourier(id);
    }

    @Step("Создание курьера с валидными параметрами")
    public Courier createCourier(String login, String password, String firstName) {
        var courier = new Courier(login, password, firstName);

        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(courier.toString())
                        .when()
                        .post();
        System.out.println(response.body().asString());
        response.then().assertThat().statusCode(201);

        return courier;
    }

    @Step("Логин курьера в системе с существующим Логином и правильным паролем")
    public String loginCourier(Courier courier) {
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body('{' + courier.getLogin() + ',' + courier.getPassword() + '}')
                        .when()
                        .post("/login");
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
                        .delete("/"+id);
        System.out.println(response.body().asString());
        response.then().assertThat().statusCode(200);
        return response.body().asString();
    }

}
