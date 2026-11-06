import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import ru.yandex.praktikum.constants.OrderListRequestParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class OrdersAcceptApiTests extends CommonTestsSteps {

    public OrdersAcceptApiTests() throws IOException {}

    @DisplayName("Успешное принятие заказа возвращает ok: true")
    @Test
    public void acceptExistedOrderResult200() {
        createCourier(validCourierJson);

        Map<String, Object> ordersParams = new HashMap<>();
        ordersParams.put(OrderListRequestParams.LIMIT_NUM.getValue(), 1);
        ordersParams.put(OrderListRequestParams.PAGE_NUM.getValue(), 0);
        var orderId = getOrdersList(ordersParams).body().path("orders[0].id").toString();

        var courierId = loginCourier(validCourierJson).path("id").toString();
        var response = acceptOrder(orderId, courierId);
        deleteCourier(courierId);
        response.then().assertThat().statusCode(200);
    }

    @DisplayName("Если не передать id курьера, запрос вернёт 400")
    @Test
    public void acceptWithEmptyCourierIdResult400() {
        Map<String, Object> ordersParams = new HashMap<>();
        ordersParams.put(OrderListRequestParams.LIMIT_NUM.getValue(), 1);
        ordersParams.put(OrderListRequestParams.PAGE_NUM.getValue(), 0);
        var orderId = getOrdersList(ordersParams).body().path("orders[0].id").toString();

        var courierId = "";
        var response = acceptOrder(orderId, courierId);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Если передать несуществующий id курьера, запрос вернёт 404")
    @Test
    public void acceptWithWrongCourierIdResult404() {
        Map<String, Object> ordersParams = new HashMap<>();
        ordersParams.put(OrderListRequestParams.LIMIT_NUM.getValue(), 1);
        ordersParams.put(OrderListRequestParams.PAGE_NUM.getValue(), 0);
        var orderId = getOrdersList(ordersParams).body().path("orders[0].id").toString();

        var courierId = "0";
        var response = acceptOrder(orderId, courierId);
        response.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @DisplayName("Если не передать id заказа, запрос вернёт 400")
    @Test
    public void acceptWithEmptyOrderIdResult400() {
        createCourier(validCourierJson);

        var orderId = "";
        var courierId = loginCourier(validCourierJson).path("id").toString();
        var response = acceptOrder(orderId, courierId);
        deleteCourier(courierId);
        response.then().assertThat().statusCode(400);
    }

    @DisplayName("Если передать несуществующий id заказа, запрос вернёт 404")
    @Test
    public void acceptWithWrongOrderIdResult404() {
        createCourier(validCourierJson);

        var orderId = "0";
        var courierId = loginCourier(validCourierJson).path("id").toString();
        var response = acceptOrder(orderId, courierId);
        deleteCourier(courierId);
        response.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Step
    public Response acceptOrder(String orderId, String courierId) {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/orders/accept/";
        var response =
                given()
                        .queryParam("courierId", courierId)
                        .when()
                        .put(orderId);
        System.out.println(response.asPrettyString());
        return response;
    }

}