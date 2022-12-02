package model;

import exceptions.EntryExistsException;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Represents a notebook containing a list of entries
public class Notebook implements Writable {

    private String name;
    private List<Entry> entries;

    // EFFECTS: constructs a notebook with the given name and a blank list of entries
    public Notebook(String name) {
        this.name = name;
        entries = new ArrayList<>();
    }

    // EFFECTS: constructs a notebook with the given name and list of entries
    // (for persistence)
    public Notebook(String name, List<Entry> entries) {
        this.name = name;
        this.entries = entries;
    }

    // EFFECTS: returns the name of the notebook
    public String getName() {
        return name;
    }

    // EFFECTS: returns the list of entries in the notebook
    public List<Entry> getEntries() {
        return entries;
    }

    // MODIFIES: this
    // EFFECTS: adds a given entry to the notebook
    public void addEntry(Entry entry) throws EntryExistsException {
        if (getEntryByWord(entry.getWord()) != null) {
            throw new EntryExistsException();
        } else {
            entries.add(entry);
            EventLog.getInstance().logEvent(new Event("Entry for \""
                    + entry.getWord()
                    + "\" added to notebook \""
                    + this.getName()
                    + "\"."));
        }
    }

    // REQUIRES: given entry is in the notebook
    // MODIFIES: this
    // EFFECTS: deletes a given entry from the notebook
    public void deleteEntry(Entry entry) {
        entries.remove(entry);
        EventLog.getInstance().logEvent(new Event("Entry for \""
                + entry.getWord()
                + "\" removed from notebook \""
                + this.getName()
                + "\"."));
    }

    // EFFECTS: returns the sorted list of all tags across all entries contained in the notebook
    public List<String> getAllTags() {
        List<String> tags = new ArrayList<>();
        for (Entry entry:entries) {
            for (String tag:entry.getTags()) {
                if (!tags.contains(tag)) {
                    tags.add(tag);
                }
            }
        }
        Collections.sort(tags);
        return tags;
    }

    // EFFECTS: takes a given list of tags and returns a list of all entries in the notebook
    // containing at least one of the tags
    public List<Entry> getEntriesTagged(List<String> tags) {
        List<Entry> taggedEntries = new ArrayList<>();
        for (Entry entry:entries) {
            for (String tag:tags) {
                if (entry.getTags().contains(tag) && !taggedEntries.contains(entry)) {
                    taggedEntries.add(entry);
                }
            }
        }
        return taggedEntries;
    }

    // EFFECTS: returns the entry of the given word or null if such an entry does not exist
    public Entry getEntryByWord(String word) {
        for (Entry entry:entries) {
            if (entry.getWord().equals(word)) {
                return entry;
            }
        }
        return null;
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("entries", entriesToJson());
        return json;
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    // EFFECTS: returns list of tags as a JSON array
    private JSONArray entriesToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Entry entry : entries) {
            jsonArray.put(entry.toJson());
        }

        return jsonArray;
    }

}
