package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotebookTest {

    private Notebook notebookA;

    @BeforeEach
    void runBefore() {
        notebookA = new Notebook("My Notebook", 100);
    }

    @Test
    void testConstructor() {
        assertEquals("My Notebook", notebookA.getName());
        assertEquals(100, notebookA.getID());
        assertEquals(null, notebookA.getEntries());
    }

    @Test
    void testAddEntry() {
        Entry entryA = new Entry("", "");
        notebookA.addEntry();
    }

    @Test
    void testDeleteEntry() {

    }
}