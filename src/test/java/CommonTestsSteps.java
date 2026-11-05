import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.yandex.praktikum.constants.CourierJsonFields;

public class CommonTestsSteps {

    public String removeParamFromJson(String json, CourierJsonFields param) {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        jsonObject.remove(param.getValue());
        return jsonObject.toString();
    }

    public String setParamToJason(String json, CourierJsonFields param, String value) {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        jsonObject.addProperty(param.getValue(), value);
        return jsonObject.toString();
    }
}
