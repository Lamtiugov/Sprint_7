import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ru.yandex.praktikum.constants.OrderListRequestParams;
import ru.yandex.praktikum.constants.ScooterColors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersApiTests extends CommonTestsSteps {

    public OrdersApiTests() throws IOException {}

    @DisplayName("Можно указать цвет: BLACK, GREY, оба, пустой")
    @ParameterizedTest
    @MethodSource("scooterColors")
    public void canOrderColorScooterResult201(JSONArray array) {
        var response = createOrder(setParamToJson(validOrderJson, "color", array));
        response.then().assertThat().statusCode(201)
                .and()
                .body("track", notNullValue());
    }

    @DisplayName("Возвращается не пустой список заказов")
    @Test
    public void getAvailable10OrdersForCouriers() {
        Map<String, Object> ordersParams = new HashMap<>();
        ordersParams.put(OrderListRequestParams.LIMIT_NUM.getValue(), 10);
        ordersParams.put(OrderListRequestParams.PAGE_NUM.getValue(), 0);
        var response = getOrdersList(ordersParams);
        response.then().body("orders", notNullValue());
    }

    static Stream<Arguments> scooterColors() {
        return Stream.of(
                Arguments.of(new JSONArray(List.of(ScooterColors.BLACK.name()))),
                Arguments.of(new JSONArray(List.of(ScooterColors.GREY.name()))),
                Arguments.of(new JSONArray(List.of(ScooterColors.GREY, ScooterColors.BLACK))),
                Arguments.of(new JSONArray())
        );
    }

}