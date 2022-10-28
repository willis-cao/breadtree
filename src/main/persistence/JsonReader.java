package persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Adapted from CPSC 210 JsonSerializationDemo
// Represents a reader that reads breadtree from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads breadtree from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Breadtree read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseBreadtree(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses breadtree from JSON object and returns it
    private Breadtree parseBreadtree(JSONObject jsonObject) {
        Breadtree breadtree = new Breadtree();
        addBreadtree(breadtree, jsonObject);
        return breadtree;
    }

    // MODIFIES: breadtree
    // EFFECTS: parses notebooks from JSON object and adds them to breadtree
    private void addBreadtree(Breadtree breadtree, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("notebooks");
        for (Object json:jsonArray) {
            JSONObject nextNotebook = (JSONObject)json;
            breadtree.addNotebook(parseNotebook(nextNotebook));
        }
    }

    // MODIFIES: breadtree
    // EFFECTS: parses a notebook from JSON object and returns it
    private Notebook parseNotebook(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        JSONArray jsonArray = jsonObject.getJSONArray("entries");
        List<Entry> entries = new ArrayList<>();
        for (Object json:jsonArray) {
            JSONObject nextEntry = (JSONObject)json;
            entries.add(parseEntry(nextEntry));
        }
        return new Notebook(name, entries);
    }

    // MODIFIES: breadtree
    // EFFECTS: parses an entry from JSON object and returns it
    private Entry parseEntry(JSONObject jsonObject) {
        String name = jsonObject.getString("word");
        String definition = jsonObject.getString("definition");
        JSONArray jsonArray = jsonObject.getJSONArray("tags");
        List<String> tags = new ArrayList<>();
        for (Object json:jsonArray) {
            tags.add(json.toString());
        }
        return new Entry(name, definition, tags);
    }
}
