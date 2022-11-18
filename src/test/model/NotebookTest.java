package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotebookTest {

    private Notebook notebookA;
    private Entry entryA;
    private Entry entryB;
    private Entry entryC;

    @BeforeEach
    void runBefore() {
        notebookA = new Notebook("Notebook A");
        entryA = new Entry("Word A", "Definition A", new ArrayList<>());
        entryA.addTag("apple");
        entryB = new Entry("Word B", "Definition B", new ArrayList<>());
        entryB.addTag("banana");
        entryC = new Entry("Word C", "Definition C", new ArrayList<>());
        entryC.addTag("banana");
        entryC.addTag("orange");
    }

    @Test
    void testConstructorName() {
        Notebook notebookB = new Notebook("Notebook B");
        assertEquals("Notebook B", notebookB.getName());
        assertEquals(0, notebookB.getEntries().size());
    }

    @Test //TO DO
    void testConstructorNameEntries() {
        List<Entry> entries = new ArrayList<>();
        entries.add(entryA);
        Notebook notebookC = new Notebook("Notebook C", entries);
        assertEquals("Notebook C", notebookC.getName());
        assertEquals(1, notebookC.getEntries().size());
    }

    @Test
    void testAddEntry() {
        assertEquals(0, notebookA.getEntries().size());
        notebookA.addEntry(entryA);
        assertEquals(1, notebookA.getEntries().size());
        assertEquals(entryA, notebookA.getEntries().get(0));
    }

    @Test
    void testDeleteEntry() {
        notebookA.addEntry(entryA);
        assertEquals(1, notebookA.getEntries().size());
        notebookA.deleteEntry(entryA);
        assertEquals(0, notebookA.getEntries().size());
    }

    @Test
    void testGetAllTags() {
        notebookA.addEntry(entryA);
        notebookA.addEntry(entryB);
        notebookA.addEntry(entryC);
        assertEquals("apple", notebookA.getAllTags().get(0));
        assertEquals("banana", notebookA.getAllTags().get(1));
        assertEquals("orange", notebookA.getAllTags().get(2));
        assertEquals(3, notebookA.getAllTags().size());
    }

    @Test
    void testGetEntriesTagged() {
        notebookA.addEntry(entryA);
        notebookA.addEntry(entryB);
        notebookA.addEntry(entryC);
        List<String> tagsA = new ArrayList<>();
        tagsA.add("apple");
        List<String> tagsB = new ArrayList<>();
        tagsB.add("banana");
        List<String> tagsC = new ArrayList<>();
        tagsC.add("banana");
        tagsC.add("orange");
        assertEquals(1, notebookA.getEntriesTagged(tagsA).size());
        assertTrue(notebookA.getEntriesTagged(tagsA).contains(entryA));
        assertFalse(notebookA.getEntriesTagged(tagsA).contains(entryB));
        assertFalse(notebookA.getEntriesTagged(tagsA).contains(entryC));
        assertEquals(2, notebookA.getEntriesTagged(tagsB).size());
        assertFalse(notebookA.getEntriesTagged(tagsB).contains(entryA));
        assertTrue(notebookA.getEntriesTagged(tagsB).contains(entryB));
        assertTrue(notebookA.getEntriesTagged(tagsB).contains(entryC));
        assertEquals(2, notebookA.getEntriesTagged(tagsC).size());
        assertFalse(notebookA.getEntriesTagged(tagsB).contains(entryA));
        assertTrue(notebookA.getEntriesTagged(tagsB).contains(entryB));
        assertTrue(notebookA.getEntriesTagged(tagsB).contains(entryC));
    }

    @Test
    void testToJson() {
        notebookA.addEntry(entryA);
        JSONObject json = notebookA.toJson();
        JSONArray entries = (JSONArray)json.get("entries");
        JSONObject entry = (JSONObject)entries.get(0);
        assertEquals("Word A", entry.get("word"));
    }

}