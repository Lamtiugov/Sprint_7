import org.json.JSONArray;
import org.json.JSONObject;

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

public class OrdersApiTests {

    private String validOrderJson = Files.readString(Path.of("src/test/resources/ValidOrderParams.json"), StandardCharsets.UTF_8);

    public OrdersApiTests() throws IOException {

    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/orders";
    }

    @DisplayName("Можно указать цвет: BLACK, GREY, оба, пустой")
    @ParameterizedTest
    @MethodSource("scooterColors")
    public void canOrderColorScooterResult201(JSONArray array) {
        var jsonObj = new JSONObject(validOrderJson);
        jsonObj.put("color", array);

        var response = createOrder(jsonObj.toString());
        response.then().assertThat().statusCode(201)
                .and()
                .body("track", notNullValue());
    }

    @DisplayName("Возвращается не пустой список заказов")
    @Test
    public void getAvailable10OrdersForCouriers() {
        Map<String, Arguments> ordersParams = new HashMap<>();
        ordersParams.put(OrderListRequestParams.LIMIT_NUM.getValue(), Arguments.of(10));
        ordersParams.put(OrderListRequestParams.PAGE_NUM.getValue(), Arguments.of(0));
        var response = getOrdersList(ordersParams);
        response.then().body("orders", notNullValue());
    }

    @Step("Создание заказа")
    public Response createOrder(String orderJson) {
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(orderJson)
                        .when()
                        .post();
        System.out.println(response.body().asString());
        return response;
    }

    @Step("Получение списка заказов с указанными параметрами")
    public Response getOrdersList(Map<String, Arguments> ordersParams) {
        var response =
                given()
                        .queryParams(ordersParams)
                        .when()
                        .get();
        System.out.println(response.body().asString());
        return response;
    }

    @Test
    public void test() {
//        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validOrderJson), JsonObject.class);
//        for (var param: jsonObject.keySet()) {
//            System.out.println(param);
//            System.out.println(jsonObject.get(param));
//        }
//        System.out.println(jsonObject.get("color"));
        var jsonObj = new JSONObject(validOrderJson);
        System.out.println(jsonObj);
        jsonObj.put("color", List.of(ScooterColors.GREY.name()));
        System.out.println(jsonObj);
//        JsonArray array = new JsonArray();
//        array.add(ScooterColors.GREY.name());
//        String json = setParamToJson(validOrderJson, "color", array);
//        System.out.println(json);
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
