package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    private Entry entryA;
    private Entry entryB;
    List<String> tagsA;

    @BeforeEach
    void runBefore() {
        tagsA = new ArrayList<>();
        tagsA.add("tag_a");
        tagsA.add("tag_b");
        tagsA.add("tag_c");
        entryA = new Entry("Word A", "Definition A", tagsA);
        entryB = new Entry("Word B", "Definition B", new ArrayList<>());
    }

    @Test
    void testConstructor() {
        Entry entryC = new Entry("Word C", "Definition C", tagsA);
        assertEquals("Word C", entryC.getWord());
        assertEquals("Definition C", entryC.getDefinition());
        assertEquals(3, entryC.getTags().size());
    }

    @Test
    void testTagAsString() {
        assertEquals("tag_a, tag_b, tag_c", entryA.tagsAsString());
        assertEquals("", entryB.tagsAsString());
    }

    @Test
    void testSetWord() {
        assertEquals("Word A", entryA.getWord());
        entryA.setWord("New Word A");
        assertEquals("New Word A", entryA.getWord());
    }

    @Test
    void testSetDefinition() {
        assertEquals("Definition A", entryA.getDefinition());
        entryA.setDefinition("New Definition A");
        assertEquals("New Definition A", entryA.getDefinition());
    }

    @Test
    void testAddTag() {
        assertEquals(3, entryA.getTags().size());
        entryA.addTag("tag_d");
        assertEquals(4, entryA.getTags().size());
        assertTrue(entryA.getTags().contains("tag_d"));
        entryA.addTag("tag_d");
        assertEquals(4, entryA.getTags().size());
    }

    @Test
    void testCleanTags() {
        entryA.addTag("tag_z");
        entryA.addTag("tag_y");
        assertEquals("tag_z", entryA.getTags().get(4));
        assertEquals("tag_y", entryA.getTags().get(3));
    }

    @Test
    void testToJson() {
        JSONObject json = entryA.toJson();
        JSONArray tags = (JSONArray)json.get("tags");
        assertEquals("tag_a", tags.get(0));
    }

}
