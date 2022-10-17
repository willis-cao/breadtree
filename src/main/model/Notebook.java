package model;

import java.util.ArrayList;
import java.util.List;

//Represents a notebook containing a list of words learned from a book
public class Notebook {

    private String name;

    private List<Entry> entries;

    public Notebook(String name) {
        this.name = name;
        entries = new ArrayList<Entry>();
    }

    public String getName() {
        return name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public void deleteEntry(Entry entry) {
        entries.remove(entry);
    }

    public List<Entry> getEntriesTagged(List<String> tags) {
        List<Entry> taggedEntries = new ArrayList<>();
        for (Entry entry:entries) {
            for (String entryTag:entry.getTags()) {
                if (tags.contains(entryTag)) {
                    taggedEntries.add(entry);
                    break;
                }
            }
        }
        return taggedEntries;
    }

}
