package model;

import exceptions.NotebookExistsException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BreadtreeTest {

    private Breadtree breadtreeA;
    private Notebook notebookA;

    @BeforeEach
    void runBefore() {
        breadtreeA = new Breadtree();
        notebookA = new Notebook("Notebook A");
    }

    @Test
    void testConstructor() {
        assertEquals(0, breadtreeA.getNotebooks().size());
    }

    @Test
    void testGetNotebookByName() {
        breadtreeA.addNotebook(notebookA);
        assertEquals("Notebook A", breadtreeA.getNotebookByName("Notebook A").getName());
        assertEquals(null, breadtreeA.getNotebookByName("Notebook B"));
    }

    @Test
    void testAddNotebook() {
        breadtreeA.addNotebook(notebookA);
        assertEquals(notebookA, breadtreeA.getNotebooks().get(0));
    }

    @Test
    void testMakeNotebook() {
        try {
            breadtreeA.makeNotebook("Notebook B");
        } catch (NotebookExistsException e) {
            fail();
        }
        assertEquals("Notebook B", breadtreeA.getNotebooks().get(0).getName());

        try {
            breadtreeA.makeNotebook("Notebook B");
            fail();
        } catch (NotebookExistsException e) {

        }
        assertEquals(1, breadtreeA.getNotebooks().size());

    }

    @Test
    void testDeleteNotebook() {
        breadtreeA.addNotebook(notebookA);
        assertEquals(1, breadtreeA.getNotebooks().size());
        breadtreeA.deleteNotebook(notebookA);
        assertEquals(0, breadtreeA.getNotebooks().size());
    }

    @Test
    void testToJson() {
        breadtreeA.addNotebook(notebookA);
        JSONObject json = breadtreeA.toJson();
        JSONArray notebooks = (JSONArray)json.get("notebooks");
        JSONObject notebook = (JSONObject)notebooks.get(0);
        assertEquals("Notebook A", notebook.get("name"));
    }

}
