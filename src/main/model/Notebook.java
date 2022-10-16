package model;

import java.util.ArrayList;
import java.util.List;

//Represents a notebook containing a list of words learned from a book
public class Notebook {

    private String name;
    private int id;

    private List<Entry> entries;

    public Notebook(String name, int id) {
        this.name = name;
        this.id = id;
        entries = new ArrayList<Entry>();
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public void deleteEntry(Entry entry) {
        entries.remove(entry);
    }
    
    public void printNotebook() {
        for (Entry entry:entries) {
            System.out.println(entry.getWord() + ": " + entry.getDefinition());
        }
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public List<Entry> getEntries() {
        return entries;
    }

}
