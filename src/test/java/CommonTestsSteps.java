import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.yandex.praktikum.constants.CourierJsonFields;

public class CommonTestsSteps {

    public String removeParamFromJson(String json, CourierJsonFields param) {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        jsonObject.remove(param.getValue());
        return jsonObject.toString();
    }

    public String setParamToJson(String json, String param, String value) {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        jsonObject.addProperty(param, value);
        return jsonObject.toString();
    }

    public String setParamToJson(String json, String param, JsonArray array) {
        JsonObject jsonObject = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        jsonObject.add(param, array);
        return jsonObject.toString();
    }
}
