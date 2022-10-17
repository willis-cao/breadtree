package model;

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
    void testConstructor() {
        assertEquals("Notebook A", notebookA.getName());
        assertEquals(0, notebookA.getEntries().size());
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
}