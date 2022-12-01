package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.List;
import java.util.Collections;

// Represents a word, its definition, and its categorical tags
public class Entry implements Writable {

    private String word;
    private String definition;
    private List<String> tags;

    // EFFECTS: constructs an entry with the given word, definition, and list of tags
    public Entry(String word, String definition, List<String> tags) {
        this.word = word;
        this.definition = definition;
        this.tags = tags;
    }

    // EFFECTS: returns the word of the entry
    public String getWord() {
        return word;
    }

    // EFFECTS: returns the definition of the entry
    public String getDefinition() {
        return definition;
    }

    // EFFECTS: returns the list of tags of the entry
    public List<String> getTags() {
        return tags;
    }

    // EFFECTS: returns the list of tags as a comma-separated String
    public String tagsAsString() {
        String tagAsString = "";
        for (int i = 0; i < tags.size(); i++) {
            tagAsString += tags.get(i);
            if (!(i == tags.size() - 1)) {
                tagAsString += ", ";
            }
        }
        return tagAsString;
    }

    // MODIFIES: this
    // EFFECTS: sets the entry's word to the given word
    public void setWord(String word) {
        this.word = word;
    }

    // MODIFIES: this
    // EFFECTS: sets the entry's definition to the given definition
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    // MODIFIES: this
    // EFFECTS: sets the entry's tags to match the given list of tags
    public void setTags(List<String> tags) {
        this.tags = tags;
        cleanTags();
    }

    // MODIFIES: this
    // EFFECTS: adds the given tag to the list of tags provided it is not a duplicate,
    // and cleans the list of tags (sets all tags to lower case and sorts alphabetically)
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            cleanTags();
        }
    }

    // MODIFIES: this
    // EFFECTS: sets all tags to lower case and sorts them alphabetically
    private void cleanTags() {
        for (String tag:tags) {
            tags.set(tags.indexOf(tag), tag.toLowerCase());
        }
        Collections.sort(tags);
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("word", word);
        json.put("definition", definition);
        json.put("tags", tagsToJson());
        return json;
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    // EFFECTS: returns list of tags as a JSON array
    private JSONArray tagsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (String tag : tags) {
            jsonArray.put(tag);
        }

        return jsonArray;
    }

}
