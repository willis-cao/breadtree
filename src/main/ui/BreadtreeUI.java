package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class BreadtreeUI extends JFrame implements ActionListener, TreeSelectionListener {

    private static final String JSON_STORE = "./data/breadtree.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private Breadtree breadtree;
    private Notebook currentNotebook;

    JTree notebookTree;
    DefaultMutableTreeNode top;
    NotebookTableModel notebookTableModel;

    public BreadtreeUI() {
        super("Breadtree");

        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        breadtree = new Breadtree();
        loadBreadtree();

        currentNotebook = breadtree.getNotebooks().get(0); //placeholder notebook

        System.out.print(currentNotebook.getName());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));

        //TABBED PANE
        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        //TAB 1: NOTEBOOKS TAB
        JPanel notebooksPanel = new JPanel(new GridLayout(0, 2));
        tabbedPane.addTab("Notebooks", notebooksPanel);
        JPanel notebooksPanelLeft = new JPanel();
        JPanel notebooksPanelRight = new JPanel();
        notebooksPanel.add(notebooksPanelLeft);
        notebooksPanel.add(notebooksPanelRight);
        //tree showing the list of notebooks and the tags found in each one
        top = new DefaultMutableTreeNode("My Notebooks");
        updateNodes(top);
        notebookTree = new JTree(top);
        notebookTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        notebookTree.addTreeSelectionListener(this);
        notebooksPanelLeft.add(notebookTree);
        //table containing words, definitions, and tags of the current notebook
        notebookTableModel = new NotebookTableModel();
        JTable notebookTable = new JTable(notebookTableModel);
        notebooksPanelRight.add(notebookTable);
        notebookTableModel.updateDataFromNotebook(currentNotebook);
        // (color for debugging)
        notebooksPanelLeft.setBackground(Color.BLUE);
        notebooksPanelRight.setBackground(Color.RED);



        // EXAMPLE CODE
//        ((JPanel) getContentPane()).setBorder(new EmptyBorder(13, 13, 13, 13) );
//        setLayout(new FlowLayout());
//        JButton btn = new JButton("Change");
//        btn.setActionCommand("myButton");
//        btn.addActionListener(this); // Sets "this" object as an action listener for btn
//        // so that when the btn is clicked,
//        // this.actionPerformed(ActionEvent e) will be called.
//        // You could also set a different object, if you wanted
//        // a different object to respond to the button click
//        label = new JLabel("flag");
//        field = new JTextField(5);
//        add(field);
//        add(btn);
//        add(label);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    // Adapted from CPSC 210 JsonSerializationDemo
    // MODIFIES: this
    // EFFECTS: loads breadtree from file
    private void loadBreadtree() {
        try {
            breadtree = jsonReader.read();
            System.out.println("Loaded from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    private void updateNodes(DefaultMutableTreeNode top) {
        for (Notebook notebook:breadtree.getNotebooks()) {
            DefaultMutableTreeNode notebookNode = new DefaultMutableTreeNode(notebook.getName());
            top.add(notebookNode);
            for (String tag:notebook.getAllTags()) {
                DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode(tag);
                notebookNode.add(tagNode);
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) notebookTree.getLastSelectedPathComponent();

        if (node == null) {
            //Nothing is selected.
            return;
        }

        Object nodeInfo = node.getUserObject();
        if (((DefaultMutableTreeNode) node.getParent()).isRoot()) {
            String notebook = (String) nodeInfo;

        } else if (node.isLeaf()) {
            String tag = (String) nodeInfo;
        }
    }

    public void actionPerformed(ActionEvent e) {
//        if (e.getActionCommand().equals("myButton")) {
//            label.setText(field.getText());
//        }
    }

    public static void main(String[] args) {
        new BreadtreeUI();
    }
}
