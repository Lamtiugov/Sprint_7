import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.constants.OrderListRequestParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersTrackApiTests extends CommonTestsSteps {

    public OrdersTrackApiTests() throws IOException {}

    @DisplayName("Успешный запрос возвращает объект с заказом")
    @Test
    public void getOrderByExistedId() {
        var orderId = createOrder(validOrderJson).path("track").toString();
        var response = getOrderById(orderId);

        response.then().assertThat()
                .statusCode(200)
                .and()
                .body("order", notNullValue());
    }

    @DisplayName("Запрос без номера заказа возвращает ошибку 400")
    @Test
    public void getOrderWithoutIdResult400() {
        getOrderById("").then().assertThat()
                .statusCode(400);
    }

    @DisplayName("Запрос заказа с несуществующим номером возвращает ошибку 404")
    @Test
    public void getOrderWithNotExistedIdResult404() {
        getOrderById("0").then().assertThat()
                .statusCode(404);
    }

    @Step("Получение заказа по номеру")
    public Response getOrderById(String id) {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/orders/track";
        var response =
                given()
                        .queryParam("t", id)
                        .when()
                        .get();
        System.out.println(response.asPrettyString());
        return response;
    }
}
