package ui;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Represents a language-learning note-taking app containing a list of notebooks,
//each of which contains a list of entries (words and their definitions)
public class BreadtreeApp {

    private Scanner input;
    private int idCounter;
    private int state;
    private List<Notebook> notebooks;
    private Notebook currentNotebook;

    // MODIFIES: this
    // EFFECTS: sets idCounter, state, and notebooks fields to initial values and runs the application
    public BreadtreeApp() {
        idCounter = 0;
        state = 0;
        notebooks = new ArrayList<Notebook>();
        runBreadtree();
    }

    // MODIFIES: this
    // EFFECTS: creates a new notebook with the given name and a unique identifying number
    private void makeNotebook(String name) {
        Notebook newNotebook = new Notebook(name, generateID());
        notebooks.add(newNotebook);
    }

    // MODIFIES: this
    // EFFECTS: removes the given notebook from the list of notebooks
    private void deleteNotebook(Notebook notebook) {
        notebooks.remove(notebook);
    }

    // MODIFIES: this
    // EFFECTS: generates a unique identifier
    private int generateID() {
        idCounter++;
        return idCounter;
    }

    // The code for console-related UI below
    // (methods runBreadtree(), init(), processCommandMainMenu(), processCommandNotebookMenu,
    // displayMainMenu(), displayNotebookMenu())
    // was adapted from CPSC 210 TellerApp

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runBreadtree() {
        boolean keepGoing = true;
        String command = null;

        init();

        while (keepGoing) {
            chooseMenu(state);
            command = input.next();
            //command = command.toLowerCase();
            if (command.equals("q")) {
                keepGoing = false;
            } else {
                chooseCommand(command);
            }
        }
        //System.out.println("\nGoodbye!");
    }
    
    // MODIFIES: this
    // EFFECTS: initializes scanner and demo notebooks
    private void init() {
        input = new Scanner(System.in);
        input.useDelimiter("\n");

        //Demo notebooks
        makeNotebook("The Cat's Teacup");
        makeNotebook("The Crane of Gratitude");
    }

    // REQUIRES: state is 0 or 1
    // EFFECTS: displays a menu depending on the current state of the application
    // where 0 represents the main menu and 1 represents the notebook menu
    private void chooseMenu(int state) {
        if (state == 0) {
            displayMainMenu();
        } else if (state == 1) {
            displayNotebookMenu();
        }
    }

    // EFFECTS: processes a given command depending on the current state of the application
    // where 0 represents the main menu and 1 represents the notebook menu
    private void chooseCommand(String command) {
        if (state == 0) {
            processCommandMainMenu(command);
        } else if (state == 1) {
            processCommandNotebookMenu(command);
        }
    }
    
    // MODIFIES: this
    // EFFECTS: processes user command on the main menu
    private void processCommandMainMenu(String command) {
        boolean notebookSelected = false;
        for (Notebook notebook:notebooks) {
            if (command.equals(Integer.toString(notebooks.indexOf(notebook) + 1))) {
                state = 1;
                currentNotebook = notebook;
                notebookSelected = true;
            }
        }
        if (notebookSelected == false) {
            if (command.equals("m")) {
                menuMakeNotebook();
            } else if (command.equals("d")) {
                menuDeleteNotebook();
            } else {
                System.out.println("Invalid command. Try again?");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: processes user command on the notebook menu
    private void processCommandNotebookMenu(String command) {
        if (command.equals("e")) {
            menuEditEntry();
        } else if (command.equals("d")) {
            menuDeleteEntry();
        } else if (command.equals("r")) {
            currentNotebook = null;
            state = 0;            
        } else if (!command.equals("")) {
            String newWord = command;
            System.out.println("Enter the definition for " + newWord);
            command = input.next();
            String newDefinition = command;
            Entry entry = new Entry(newWord, newDefinition);
            currentNotebook.addEntry(entry);
        }
    }
    
    // EFFECTS: displays the main menu to user
    private void displayMainMenu() {
        System.out.println("\nYour notebooks:");
        String listOfNotebooksText = "";
        int notebookCounter = 1;
        for (Notebook notebook:notebooks) {
            listOfNotebooksText += "\n(" + notebookCounter + ") " + notebook.getName();
            notebookCounter++;
        }
        System.out.println(listOfNotebooksText);
        System.out.println("\nEnter the number for the notebook you wish to access or choose from the options below:");
        System.out.println("\t[m]ake a new notebook");
        System.out.println("\t[d]elete a notebook");
        System.out.println("\t[q]uit");
    }

    // EFFECTS: displays the contents of a notebook and the notebook menu to user
    private void displayNotebookMenu() {
        System.out.println(currentNotebook.getName());
        List<Entry> entries = currentNotebook.getEntries();
        for (Entry entry:entries) {
            System.out.println("(" + Integer.toString(entries.indexOf(entry) + 1) + ") "
                                   + entry.getWord() + ": " + entry.getDefinition());
        }
        System.out.println(
                "Enter a new word below or select from one of the following options: "
                        + "[e]dit, [d]elete, [r]eturn to main menu");
    }

    // MODIFIES: this
    // EFFECTS: makes a new notebook with the given name provided by the user in the console
    private void menuMakeNotebook() {
        System.out.println("Enter the name for your new notebook:");
        makeNotebook(input.next());
    }

    // MODIFIES: this
    // EFFECTS: deletes the notebook selected by the user in the console
    private void menuDeleteNotebook() {
        System.out.println("Enter the number of the notebook to delete");
        String selection = input.next();
        try {
            Notebook selectedNotebook = notebooks.get(Integer.parseInt(selection) - 1);
            System.out.println(
                    "Are you sure you want to delete the notebook below [y/n]? This action cannot be reversed.");
            System.out.println(selectedNotebook.getName());
            selection = input.next();
            if (selection.equals("y")) {
                deleteNotebook(selectedNotebook);
                System.out.println("Notebook was deleted.");
            } else if (selection.equals("n")) {
                System.out.println("Notebook was not deleted.");
            }
        } catch (Exception e) {
            System.out.println("Invalid notebook!");
        }
    }

    // MODIFIES: this
    // EFFECTS: edits the word or definition of the entry selected by the user in the console
    private void menuEditEntry() {
        System.out.println("Enter the number of the entry to edit:");
        String selection = input.next();
        try {
            Entry selectedEntry = currentNotebook.getEntries().get(Integer.parseInt(selection) - 1);
            System.out.println("Edit the [w]ord or [d]efinition?");
            selection = input.next();
            if (selection.equals("w")) {
                System.out.println("Change " + selectedEntry.getWord() + " to?");
                selection = input.next();
                selectedEntry.setWord(selection);
            } else if (selection.equals("d")) {
                System.out.println("Change " + selectedEntry.getDefinition() + " to?");
                selection = input.next();
                selectedEntry.setDefinition(selection);
            } else {
                System.out.println("Invalid command. Try again?");
            }
        } catch (Exception e) {
            System.out.println("Invalid entry!");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes the entry selected by the user in the console
    private void menuDeleteEntry() {
        System.out.println("Enter the number of the entry to delete:");
        String selection = input.next();
        try {
            Entry selectedEntry = currentNotebook.getEntries().get(Integer.parseInt(selection) - 1);
            currentNotebook.deleteEntry(selectedEntry);
        } catch (Exception e) {
            System.out.println("Invalid entry!");
        }
    }
}
