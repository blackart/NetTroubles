package test;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import menu.Group;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class testJSON {
    public static void main(String[] args) throws IOException {
        File file = new File("./src/test/webapp/menu.json");
        System.out.println(file.getCanonicalPath());
        FileReader fileReader = new FileReader(file);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(fileReader);

        JsonArray groups = jsonObject.getAsJsonArray("groups");

        List<Group> obj_groups = new ArrayList<Group>();

        Gson gson = new Gson();
        for (int i = 0; i < groups.size(); i++) {
            obj_groups.add(gson.fromJson(groups.get(i), Group.class));
        }



        System.out.print("end");
    }
}
