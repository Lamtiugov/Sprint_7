import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class OrdersAcceptApiTests extends CommonTestsSteps {

    public OrdersAcceptApiTests() throws IOException {}

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru/api/v1/orders";
    }

    @DisplayName("Успешное принятие заказа возвращает ok: true")
    @Test
    public void acceptExistedOrder() {
        var orderId = createOrder(validOrderJson).path("track").toString();

    }

}
