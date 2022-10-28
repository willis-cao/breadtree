package persistence;

import org.json.JSONObject;

// Adapted from CPSC 210 JsonSerializationDemo
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
