package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Adapted from CPSC 210 JsonSerializationDemo
class JsonReaderTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Breadtree breadtree = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyBreadtree() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyBreadtree.json");
        try {
            Breadtree breadtree = reader.read();
            assertEquals(0, breadtree.getNotebooks().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralBreadtree() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralBreadtree.json");
        try {
            Breadtree breadtree = reader.read();
            List<Notebook> notebooks = breadtree.getNotebooks();
            assertEquals(2, notebooks.size());
            Notebook notebook = notebooks.get(0);
            assertEquals("The Cat's Teacup", notebook.getName());
            assertEquals(3, notebook.getEntries().size());
            Entry entry = notebook.getEntries().get(0);
            assertEquals("word1", entry.getWord());
            assertEquals("def1", entry.getDefinition());
            List<String> tags = entry.getTags();
            assertEquals(3, tags.size());
            assertEquals("tag1", tags.get(0));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}