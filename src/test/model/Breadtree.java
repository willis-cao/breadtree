package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

//Represents a list of notebooks
public class Breadtree implements Writable {

    private List<Notebook> notebooks;

    // EFFECTS: constructs a Breadtree representing an (empty) list of notebooks
    public Breadtree() {
        notebooks = new ArrayList<>();
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("notebooks", notebooksToJson());
        return json;
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    // EFFECTS: returns list of notebooks as a JSON array
    private JSONArray notebooksToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Notebook notebook : notebooks) {
            jsonArray.put(notebook.toJson());
        }

        return jsonArray;
    }
}
