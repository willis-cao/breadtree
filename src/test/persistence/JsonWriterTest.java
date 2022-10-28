package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Adapted from CPSC 210 JsonSerializationDemo
class JsonWriterTest {

    @Test
    void testWriterInvalidFile() {
        try {
            Breadtree breadtree = new Breadtree();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyBreadtree() {
        try {
            Breadtree breadtree = new Breadtree();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyBreadtree.json");
            writer.open();
            writer.write(breadtree);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyBreadtree.json");
            breadtree = reader.read();
            assertEquals(0, breadtree.getNotebooks().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralBreadtree() {
        try {
            Breadtree breadtree = new Breadtree();
            List<Entry> entries = new ArrayList<>();
            List<String> tags = new ArrayList<>();
            tags.add("tag");
            Entry entry = new Entry("word", "definition", tags);
            entries.add(entry);
            Notebook notebook = new Notebook("Notebook", entries);
            breadtree.addNotebook(notebook);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralBreadtree.json");
            writer.open();
            writer.write(breadtree);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralBreadtree.json");
            breadtree = reader.read();
            notebook = breadtree.getNotebooks().get(0);
            entry = notebook.getEntries().get(0);
            assertEquals(1, breadtree.getNotebooks().size());
            assertEquals("Notebook", notebook.getName());
            assertEquals(1, notebook.getEntries().size());
            assertEquals("word", entry.getWord());
            assertEquals("definition", entry.getDefinition());
            assertEquals(1, entry.getTags().size());
            assertEquals("tag", entry.getTags().get(0));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}