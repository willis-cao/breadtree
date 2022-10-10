package model;

//Represents a word and its definition
public class Entry {

    private String word;
    private String definition;

    public Entry(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }
}