package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotebookTest {

    private Notebook notebookA;

    @BeforeEach
    void runBefore() {
        notebookA = new Notebook("My Notebook");
    }

    @Test
    void testConstructor() {
        assertEquals("My Notebook", notebookA.getName());
        assertEquals(null, notebookA.getEntries());
    }

    @Test
    void testAddEntry() {
        Entry entryA = new Entry("", "", null);

    }

    @Test
    void testDeleteEntry() {

    }
}