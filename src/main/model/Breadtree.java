package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a collection of notebooks
public class Breadtree implements Writable {

    private List<Notebook> notebooks;

    // EFFECTS: constructs a Breadtree representing an (empty) list of notebooks
    public Breadtree() {
        notebooks = new ArrayList<>();
    }

    // EFFECTS: returns the list of notebooks
    public List<Notebook> getNotebooks() {
        return notebooks;
    }

    // REQUIRES: a notebook with the given name exists
    // EFFECTS: returns the notebook with the given name
    public Notebook getNotebookByName(String name) {
        for (Notebook notebook:notebooks) {
            if (notebook.getName().equals(name)) {
                return notebook;
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: adds the given notebook to the list of notebooks
    public void addNotebook(Notebook notebook) {
        notebooks.add(notebook);
    }

    // MODIFIES: this
    // EFFECTS: creates a new notebook with the given name and adds it to the list of notebooks
    public void makeNotebook(String name) {
        Notebook newNotebook = new Notebook(name);
        EventLog.getInstance().logEvent(new Event("Notebook \"" + name + "\" was created."));
        addNotebook(newNotebook);
    }

    // MODIFIES: this
    // EFFECTS: removes the given notebook from the list of notebooks
    public void deleteNotebook(Notebook notebook) {
        notebooks.remove(notebook);
        EventLog.getInstance().logEvent(new Event("Notebook \"" + notebook.getName() + "\" was deleted."));
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
