package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

//Represents a breadtree containing a list of notebooks
public class Breadtree implements Writable {

    private List<Notebook> notebooks;

    // EFFECTS: constructs a breadtree representing an (empty) list of notebooks
    public Breadtree() {
        notebooks = new ArrayList<>();
    }

    // EFFECTS: returns the list of notebooks
    public List<Notebook> getNotebooks() {
        return notebooks;
    }

    // MODIFIES: this
    // EFFECTS: adds the given notebook to the list of notebooks
    // (for persistence)
    public void addNotebook(Notebook notebook) {
        notebooks.add(notebook);
    }

    // MODIFIES: this
    // EFFECTS: creates a new notebook with the given name and adds it to the list of notebooks
    public void makeNotebook(String name) {
        Notebook newNotebook = new Notebook(name);
        notebooks.add(newNotebook);
    }

    // MODIFIES: this
    // EFFECTS: removes the given notebook from the list of notebooks
    public void deleteNotebook(Notebook notebook) {
        notebooks.remove(notebook);
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
