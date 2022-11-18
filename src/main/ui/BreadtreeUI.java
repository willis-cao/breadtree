package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BreadtreeUI extends JFrame implements ActionListener, TreeSelectionListener {

    private static final String JSON_STORE = "./data/breadtree.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private Breadtree breadtree;
    private Notebook currentNotebook;
    private boolean isViewingNotebook;

    JTabbedPane tabbedPane;
    JPanel notebooksPanel;
    JPanel notebooksPanelLeft;
    JPanel notebooksPanelRight;
    DefaultMutableTreeNode top;
    JTree notebookTree;
    JScrollPane treeView;
    JTable notebookTable;
    JScrollPane tableView;
    NotebookTableModel notebookTableModel;
    JTextField entryField;
    JPanel buttonPanel;
    JButton newNotebookButton;
    JButton deleteNotebookButton;
    JButton saveButton;

    public BreadtreeUI() {
        super("Breadtree");

        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        breadtree = new Breadtree();
        loadBreadtree();

        currentNotebook = breadtree.getNotebooks().get(0); //placeholder notebook

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));

        //TABBED PANE
        tabbedPane = new JTabbedPane();
        add(tabbedPane);

        setupTabNotebooks();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        // (color for debugging)
        notebooksPanelLeft.setBackground(Color.BLUE);
        notebooksPanelRight.setBackground(Color.RED);
    }

    public void setupTabNotebooks() {
        //TAB 1: NOTEBOOKS TAB
        notebooksPanel = new JPanel();
        notebooksPanel.setLayout(new BoxLayout(notebooksPanel, BoxLayout.LINE_AXIS));
        tabbedPane.addTab("Notebooks", notebooksPanel);
        notebooksPanelLeft = new JPanel();
        notebooksPanelLeft.setLayout(new BoxLayout(notebooksPanelLeft, BoxLayout.PAGE_AXIS));
        notebooksPanelLeft.setPreferredSize(new Dimension(220, 600));
        notebooksPanelRight = new JPanel();
        notebooksPanelRight.setLayout(new BoxLayout(notebooksPanelRight, BoxLayout.PAGE_AXIS));

        notebooksPanel.add(notebooksPanelLeft);
        notebooksPanel.add(notebooksPanelRight);

        setupTree();
        setupTable();
        setupEntryField();
        setupButtons();
    }

    private void setupTree() {
        //tree showing the list of notebooks and the tags found in each one
        top = new DefaultMutableTreeNode("My Notebooks");
        updateNodes(top);
        notebookTree = new JTree(top);
        treeView = new JScrollPane(notebookTree);
        notebookTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        notebookTree.addTreeSelectionListener(this);
        notebooksPanelLeft.add(treeView);
    }

    private void setupTable() {
        //table containing words, definitions, and tags of the current notebook
        notebookTableModel = new NotebookTableModel();
        notebookTable = new JTable(notebookTableModel);
        tableView = new JScrollPane(notebookTable);
        notebooksPanelRight.add(tableView);
        notebookTableModel.updateDataFromNotebook(currentNotebook);
        notebookTableModel.fireTableDataChanged();
    }

    private void setupEntryField() {
        //text field for creating and editing entries
        entryField = new JTextField();
        notebooksPanelRight.add(entryField);
        entryField.addActionListener(this);
        entryField.setActionCommand("entryField");
    }

    private void setupButtons() {
        buttonPanel = new JPanel(new GridBagLayout());
        //buttonPanel.setBorder(new TitledBorder("Notebook Options"));
        GridBagConstraints c = new GridBagConstraints();
        newNotebookButton = new JButton("New");
        deleteNotebookButton = new JButton("Delete");
        saveButton = new JButton("Save");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        buttonPanel.add(newNotebookButton, c);
        c.gridx = 1;
        buttonPanel.add(deleteNotebookButton, c);
        c.gridx = 2;
        buttonPanel.add(saveButton, c);
        notebooksPanelLeft.add(buttonPanel);
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
            System.out.println(notebook);
            notebookTableModel.updateDataFromNotebook(breadtree.getNotebookByName(notebook));
            notebookTableModel.fireTableDataChanged();
        } else if (node.isLeaf()) {
            System.out.println("is leaf!");
            String tag = (String) nodeInfo;
            List<String> tagInList = new ArrayList<>();
            tagInList.add(tag);
            DefaultMutableTreeNode parentNotebookNode = (DefaultMutableTreeNode) node.getParent();
            Object parentNodeInfo = parentNotebookNode.getUserObject();
            Notebook parentNotebook = breadtree.getNotebookByName((String) parentNodeInfo);
            Notebook filteredParentNotebook = new Notebook("", parentNotebook.getEntriesTagged(tagInList));
            notebookTableModel.updateDataFromNotebook(filteredParentNotebook);
            notebookTableModel.fireTableDataChanged();
        }
    }

    public void actionPerformed(ActionEvent e) {
//        if (e.getActionCommand().equals("myButton")) {
//            label.setText(field.getText());
//        }
        if (e.getActionCommand().equals("entryField")) {
            System.out.println(entryField.getText());
        }
    }

    public static void main(String[] args) {
        new BreadtreeUI();
    }
}
