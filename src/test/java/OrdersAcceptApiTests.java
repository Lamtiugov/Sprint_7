import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class OrdersAcceptApiTests extends CommonTestsSteps {

    public OrdersAcceptApiTests() throws IOException {}

    @DisplayName("Успешное принятие заказа возвращает ok: true")
    @Test
    public void acceptExistedOrder() {
        var orderId = createOrder(validOrderJson).path("track").toString();
        createCourier(validCourierJson);
        var courierId = loginCourier(validCourierJson).path("id").toString();
        deleteCourier(courierId);
        System.out.println(orderId + " " + courierId);
    }

}
