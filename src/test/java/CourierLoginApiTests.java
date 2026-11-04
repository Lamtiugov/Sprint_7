import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CourierLoginApiTests {

    private String validCourierJson = Files.readString(Path.of("src/test/resources/ValidCourierParams.json"), StandardCharsets.UTF_8);

    public CourierLoginApiTests() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/courier";
    }

    @Test
    public void test() {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(validCourierJson), JsonObject.class);
        jsonObject.remove("firstName");
        System.out.println(jsonObject.toString());
    }
}
