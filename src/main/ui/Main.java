package ui;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        // Adapted from CPSC 210 JsonSerializationDemo
        try {
            new BreadtreeUI();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to run application: file not found");
        }
    }

}
