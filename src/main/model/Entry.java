package model;

import java.util.List;
import java.util.Collections;

//Represents a word, its definition, and its categorical tags
public class Entry {

    private String word;
    private String definition;
    private List<String> tags;

    public Entry(String word, String definition, List<String> tags) {
        this.word = word;
        this.definition = definition;
        this.tags = tags;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public List<String> getTags() {
        return tags;
    }

    public String printTags() {
        String tagString = "";
        for (int i = 0; i < tags.size(); i++) {
            tagString += tags.get(i);
            if (!(i == tags.size() - 1)) {
                tagString += ", ";
            }
        }
        return tagString;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            cleanTags();
        }
    }

    private void cleanTags() {
        for (String tag:tags) {
            tags.set(tags.indexOf(tag), tag.toLowerCase());
        }
        Collections.sort(tags);
    }
}
