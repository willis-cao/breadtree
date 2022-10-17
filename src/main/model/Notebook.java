package model;

import java.util.ArrayList;
import java.util.List;

//Represents a notebook containing a list of words learned from a book
public class Notebook {

    private String name;
    private List<Entry> entries;

    // EFFECTS: constructs a notebook with the given name and a blank list of entries
    public Notebook(String name) {
        this.name = name;
        entries = new ArrayList<>();
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
    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    // REQUIRES: given entry is in the notebook
    // MODIFIES: this
    // EFFECTS: deletes a given entry from the notebook
    public void deleteEntry(Entry entry) {
        entries.remove(entry);
    }

    // EFFECTS: takes a given list of tags and returns a list of all entries in the notebook
    // having one or more of those tags
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

}
