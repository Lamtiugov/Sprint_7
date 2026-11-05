import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommonTestsSteps {

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
