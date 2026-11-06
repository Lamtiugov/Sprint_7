import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.yandex.praktikum.constants.CourierJsonFields;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;

public class CommonTestsSteps {

    protected String validCourierJson = Files.readString(Path.of("src/test/resources/ValidCourierParams.json"), StandardCharsets.UTF_8);

    public CommonTestsSteps() throws IOException {
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
        System.out.println(response.asPrettyString());
        return response;
    }

    @Step("Логин курьера")
    public Response loginCourier(String courierJson) {
        courierJson = removeParamFromJson(courierJson, CourierJsonFields.FIRST_NAME.getValue());
        var response =
                given()
                        .header("Content-type", "application/json")
                        .auth().none()
                        .body(courierJson)
                        .when()
                        .post("login");
        System.out.println(response.asPrettyString());
        return response;
    }

    @Step("Удалить курьера по ID")
    // Хотя по спеке тут json нужен...
    Response deleteCourier(String id) {
        var response =
                given()
                        .when()
                        .delete(id);
        System.out.println(response.asPrettyString());
        return response;
    }

    public String removeParamFromJson(String json, String param) {
        var jsonObj = new JSONObject(json);
        jsonObj.remove(param);
        return jsonObj.toString();
    }

    public String setParamToJson(String json, String param, String value) {
        var jsonObj = new JSONObject(json);
        jsonObj.put(param, value);
        return jsonObj.toString();
    }

    public String setParamToJson(String json, String param, JSONArray array) {
        var jsonObj = new JSONObject(json);
        jsonObj.put(param, array);
        return jsonObj.toString();
    }
}