//package ui;
//
//import model.Breadtree;
//import model.Entry;
//import model.Notebook;
//import persistence.JsonReader;
//import persistence.JsonWriter;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
//// Represents a language-learning note-taking app containing a breadtree, or list of notebooks,
//// each of which contains a list of entries (each containing a word, definition, and tags)
//public class BreadtreeApp {
//
//    private Scanner input;
//    private int state;
//    private Breadtree breadtree;
//    private Notebook currentNotebook;
//
//    private static final String JSON_STORE = "./data/breadtree.json";
//    private JsonWriter jsonWriter;
//    private JsonReader jsonReader;
//
//    // MODIFIES: this
//    // EFFECTS: sets state and notebooks fields to initial values and runs the application
//    public BreadtreeApp() throws FileNotFoundException {
//        state = 0;
//        breadtree = new Breadtree();
//        jsonWriter = new JsonWriter(JSON_STORE);
//        jsonReader = new JsonReader(JSON_STORE);
//        loadBreadtree();
//        runBreadtree();
//    }
//
//    // The code for console-related UI below
//    // (methods runBreadtree(), init(), processCommandMainMenu(), processCommandNotebookMenu,
//    // displayMainMenu(), displayNotebookMenu())
//    // was adapted from CPSC 210 TellerApp
//
//    // MODIFIES: this
//    // EFFECTS: processes user input
//    private void runBreadtree() {
//        boolean keepGoing = true;
//        String command;
//        init();
//        while (keepGoing) {
//            chooseMenu();
//            command = input.next();
//            if (command.equals("q")) {
//                keepGoing = false;
//            } else {
//                chooseCommand(command);
//            }
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS: initializes scanner
//    private void init() {
//        input = new Scanner(System.in);
//        input.useDelimiter("\n");
//    }
//
//    // EFFECTS: displays a menu depending on the current state of the application
//    // where 0 represents the main menu and 1 represents the notebook menu
//    private void chooseMenu() {
//        if (state == 0) {
//            displayMainMenu();
//        } else if (state == 1) {
//            displayNotebookMenu();
//        }
//    }
//
//    // EFFECTS: processes a given command depending on the current state of the application
//    // where 0 represents the main menu and 1 represents the notebook menu
//    private void chooseCommand(String command) {
//        if (state == 0) {
//            processCommandMainMenu(command);
//        } else if (state == 1) {
//            processCommandNotebookMenu(command);
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS: processes user command on the main menu
//    // including selecting, making, or deleting a notebook
//    private void processCommandMainMenu(String command) {
//        boolean notebookSelected = false;
//        List<Notebook> notebooks = breadtree.getNotebooks();
//        for (Notebook notebook:notebooks) {
//            if (command.equals(Integer.toString(notebooks.indexOf(notebook) + 1))) {
//                state = 1;
//                currentNotebook = notebook;
//                notebookSelected = true;
//            }
//        }
//        if (!notebookSelected) {
//            if (command.equals("m")) {
//                menuMakeNotebook();
//            } else if (command.equals("d")) {
//                menuDeleteNotebook();
//            } else if (command.equals("s")) {
//                saveBreadtree();
//            } else {
//                System.out.println("Invalid command. Try again?");
//            }
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS: processes user command on the notebook menu
//    // including adding, editing, or deleting an entry, or returning to the main menu
//    private void processCommandNotebookMenu(String command) {
//        if (command.equals("e")) {
//            menuEditEntry();
//        } else if (command.equals("d")) {
//            menuDeleteEntry();
//        } else if (command.equals("r")) {
//            currentNotebook = null;
//            state = 0;
//        } else if (!command.equals("")) {
//            String[] wordComponents = command.split("\\s*,\\s*");
//            List<String> tags = new ArrayList<>();
//            for (int i = 2; i < wordComponents.length; i++) {
//                tags.add(wordComponents[i]);
//            }
//            Entry entry = new Entry(wordComponents[0], wordComponents[1], new ArrayList<>());
//            for (String tag:tags) {
//                entry.addTag(tag);
//            }
//            currentNotebook.addEntry(entry);
//        }
//    }
//
//    // EFFECTS: displays the main menu to user
//    private void displayMainMenu() {
//        System.out.println("\nYour notebooks:");
//        String listOfNotebooksText = "";
//        int notebookCounter = 1;
//        List<Notebook> notebooks = breadtree.getNotebooks();
//        for (Notebook notebook:notebooks) {
//            listOfNotebooksText += "\n(" + notebookCounter + ") " + notebook.getName();
//            notebookCounter++;
//        }
//        System.out.println(listOfNotebooksText);
//        System.out.println("\nEnter the number for the notebook you wish to access or choose from the options below:");
//        System.out.println("\t[m]ake a new notebook");
//        System.out.println("\t[d]elete a notebook");
//        System.out.println("\t[s]ave state");
//        System.out.println("\t[q]uit");
//    }
//
//    // EFFECTS: displays the contents of a notebook (a list of words, their definitions, and tags)
//    // and the notebook menu to user
//    private void displayNotebookMenu() {
//        System.out.println(currentNotebook.getName());
//        List<Entry> entries = currentNotebook.getEntries();
//        for (Entry entry:entries) {
//            System.out.println("(" + (entries.indexOf(entry) + 1) + ") "
//                                    + entry.getWord() + ": " + entry.getDefinition() + " ["
//                                    + entry.tagsAsString() + "]");
//        }
//        System.out.println("\nEnter the information for a new word (e.g., word, definition, tag1, tag2, etc.)");
//        System.out.println("or select from one of the following options: [e]dit, [d]elete, [r]eturn to main menu");
//
//    }
//
//    // MODIFIES: this
//    // EFFECTS: makes a new notebook with the given name provided by the user in the console
//    private void menuMakeNotebook() {
//        System.out.println("Enter the name for your new notebook:");
//        breadtree.makeNotebook(input.next());
//    }
//
//    // MODIFIES: this
//    // EFFECTS: deletes the notebook selected by the user in the console
//    private void menuDeleteNotebook() {
//        System.out.println("Enter the number of the notebook to delete");
//        String selection = input.next();
//        try {
//            List<Notebook> notebooks = breadtree.getNotebooks();
//            Notebook selectedNotebook = notebooks.get(Integer.parseInt(selection) - 1);
//            System.out.println(
//                    "Are you sure you want to delete the notebook below [y/n]? This action cannot be reversed.");
//            System.out.println(selectedNotebook.getName());
//            selection = input.next();
//            if (selection.equals("y")) {
//                breadtree.deleteNotebook(selectedNotebook);
//                System.out.println("Notebook was deleted.");
//            } else if (selection.equals("n")) {
//                System.out.println("Notebook was not deleted.");
//            }
//        } catch (Exception e) {
//            System.out.println("Invalid notebook!");
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS: edits the word or definition of the entry selected by the user in the console
//    private void menuEditEntry() {
//        System.out.println("Enter the number of the entry to edit:");
//        String selection = input.next();
//        try {
//            Entry selectedEntry = currentNotebook.getEntries().get(Integer.parseInt(selection) - 1);
//            System.out.println("Edit the [w]ord or [d]efinition?");
//            selection = input.next();
//            if (selection.equals("w")) {
//                System.out.println("Change " + selectedEntry.getWord() + " to?");
//                selection = input.next();
//                selectedEntry.setWord(selection);
//            } else if (selection.equals("d")) {
//                System.out.println("Change " + selectedEntry.getDefinition() + " to?");
//                selection = input.next();
//                selectedEntry.setDefinition(selection);
//            } else {
//                System.out.println("Invalid command. Try again?");
//            }
//        } catch (Exception e) {
//            System.out.println("Invalid entry!");
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS: deletes the entry selected by the user in the console
//    private void menuDeleteEntry() {
//        System.out.println("Enter the number of the entry to delete:");
//        String selection = input.next();
//        try {
//            Entry selectedEntry = currentNotebook.getEntries().get(Integer.parseInt(selection) - 1);
//            currentNotebook.deleteEntry(selectedEntry);
//        } catch (Exception e) {
//            System.out.println("Invalid entry!");
//        }
//    }
//
//    // Adapted from CPSC 210 JsonSerializationDemo
//    // EFFECTS: saves breadtree to file
//    private void saveBreadtree() {
//        try {
//            jsonWriter.open();
//            jsonWriter.write(breadtree);
//            jsonWriter.close();
//            System.out.println("Saved to " + JSON_STORE);
//        } catch (FileNotFoundException e) {
//            System.out.println("Unable to write to file: " + JSON_STORE);
//        }
//    }
//
//    // Adapted from CPSC 210 JsonSerializationDemo
//    // MODIFIES: this
//    // EFFECTS: loads breadtree from file
//    private void loadBreadtree() {
//        try {
//            breadtree = jsonReader.read();
//            System.out.println("Loaded from " + JSON_STORE);
//        } catch (IOException e) {
//            System.out.println("Unable to read from file: " + JSON_STORE);
//        }
//    }
//}
