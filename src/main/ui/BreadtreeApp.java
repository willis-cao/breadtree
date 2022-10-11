package ui;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Represents a language-learning note-taking app containing a list of notebooks
public class BreadtreeApp {

    private Scanner input;

    private List<Notebook> notebooks;
    private int idCounter;

    public BreadtreeApp() {
        idCounter = 0;
        notebooks = new ArrayList<Notebook>();
        runBreadtree();
    }

    private void makeNotebook(String name) {
        Notebook newNotebook = new Notebook(name, generateID());
        notebooks.add(newNotebook);
    }

    private void deleteNotebook(Notebook notebook) {
        notebooks.remove(notebook);
    }

    private int generateID() {
        idCounter++;
        return idCounter;
    }

    // The code for console-related UI below (runBreadtree(), init(), processCommand(), displayMenu())
    // was adapted from CPSC 210 TellerApp

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runBreadtree() {
        boolean keepGoing = true;
        String command = null;

        init();

        while (keepGoing) {
            displayMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        //System.out.println("\nGoodbye!");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processCommand(String command) {
        boolean notebookSelected = false;
        for (Notebook notebook:notebooks) {
            if (command.equals(Integer.toString(notebooks.indexOf(notebook) + 1))) {
                notebook.printNotebook();
                notebookSelected = true;
            }
        }
        if (notebookSelected == false) {
            if (command.equals("n")) {
                menuMakeNotebook();
            } else if (command.equals("d")) {
                menuDeleteNotebook();
            } else if (command.equals("q")) {
                System.out.println("q was pressed");
            } else {
                System.out.println("Invalid command. Try again?");
            }
        }
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

    // EFFECTS: displays menu of options to user
    private void displayMenu() {
        System.out.println("\nYour notebooks:");
        String listOfNotebooksText = "";
        int notebookCounter = 1;
        for (Notebook notebook:notebooks) {
            listOfNotebooksText += "\n(" + notebookCounter + ") " + notebook.getName();
            notebookCounter++;
        }
        System.out.println(listOfNotebooksText);
        System.out.println("\nEnter the number for the notebook you wish to access or choose from the options below:");
        System.out.println("\tn -> make a new notebook");
        System.out.println("\td -> delete a notebook");
        System.out.println("\tq -> quit");
    }

    private void menuMakeNotebook() {
        System.out.println("Enter the name for your new notebook:");
        makeNotebook(input.next());
    }

    private void menuDeleteNotebook() {
        System.out.println("Enter the number of the notebook to delete");
        String selection = input.next();
        for (Notebook notebook:notebooks) {
            if (selection.equals(Integer.toString(notebooks.indexOf(notebook) + 1))) {
                System.out.println("Are you sure you want to delete the notebook below (y/n)? This action cannot be reversed.");
                System.out.println(notebook.getName());
                selection = input.next();
                if (selection.equals("y")) {
                    deleteNotebook(notebook);
                    System.out.println("Notebook was deleted.");
                } else if (selection.equals("n")) {
                    System.out.println("Notebook was not deleted.");
                }
            }
        }

    }
}
