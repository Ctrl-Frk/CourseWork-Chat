package resources;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JsonParser {
    private static final String settingsPath = "src/main/java/resources/settings.json"; //Путь к настройкам

    public static String parseJson(String key) {
        try {
            Object obj = new JSONParser().parse(new FileReader(settingsPath)); //Парсим в Object
            JSONObject jsonObject = (JSONObject) obj; //Кастуем к JSONObject
            return (String) jsonObject.get(key); //Отдаем строку по ключу
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
