package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Represents a graphical user interface for the Breadtree application
public class BreadtreeUI extends JFrame implements ActionListener, TreeSelectionListener, ListSelectionListener {

    private static final String JSON_STORE = "./data/breadtree.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private Breadtree breadtree;
    private Notebook currentNotebook;
    private boolean isViewingNotebook;

    JTabbedPane tabbedPane;
    JPanel notebooksPanelVertical;
    JPanel notebooksPanelHorizontal;
    JPanel notebooksPanelLeft;
    JPanel notebooksPanelRight;
    DefaultMutableTreeNode rootNode;
    JTree notebookTree;
    JScrollPane treeView;
    JTable notebookTable;
    ListSelectionModel tableSelectionModel;
    JScrollPane tableView;
    NotebookTableModel notebookTableModel;
    JLabel dynamicLabel;
    JTextField entryField;
    JPanel buttonPanel;
    JButton newNotebookButton;
    JButton deleteNotebookButton;
    JButton saveButton;

    // EFFECTS: constructs and initializes a GUI for the breadtree application
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
    }

    // MODIFIES: this
    // EFFECTS: sets up the "Notebooks" tab of the GUI (currently the only tab!)
    public void setupTabNotebooks() {
        //TAB 1: NOTEBOOKS TAB
        notebooksPanelVertical = new JPanel();
        notebooksPanelVertical.setLayout(new BoxLayout(notebooksPanelVertical, BoxLayout.PAGE_AXIS));
        notebooksPanelVertical.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Notebooks", notebooksPanelVertical);
        notebooksPanelHorizontal = new JPanel();
        notebooksPanelHorizontal.setLayout(new BoxLayout(notebooksPanelHorizontal, BoxLayout.LINE_AXIS));
        notebooksPanelVertical.add(notebooksPanelHorizontal);
        notebooksPanelLeft = new JPanel();
        notebooksPanelLeft.setLayout(new BoxLayout(notebooksPanelLeft, BoxLayout.PAGE_AXIS));
        notebooksPanelLeft.setBorder(new EmptyBorder(0, 0, 5, 5));
        notebooksPanelRight = new JPanel();
        notebooksPanelRight.setLayout(new BoxLayout(notebooksPanelRight, BoxLayout.PAGE_AXIS));
        notebooksPanelRight.setBorder(new EmptyBorder(0, 5, 5, 0));
        notebooksPanelHorizontal.add(notebooksPanelLeft);
        notebooksPanelHorizontal.add(notebooksPanelRight);

        setupDynamicLabel();
        setupTree();
        setupButtons();
        setupTable();
        setupEntryField();
    }

    // MODIFIES: this
    // EFFECTS: sets up the dynamic label at the bottom of the application that provides
    // the user with information about the status of the application
    private void setupDynamicLabel() {
        dynamicLabel = new JLabel("");
        dynamicLabel.setText("Test text...");
        Box dynamicLabelBox = Box.createHorizontalBox();
        dynamicLabelBox.add(dynamicLabel);
        dynamicLabelBox.add(Box.createHorizontalGlue());
        notebooksPanelVertical.add(dynamicLabelBox);
    }

    // MODIFIES: this
    // EFFECTS: sets up the tree at the left of the application that represents the user's
    // notebooks and the tags contained in each one
    private void setupTree() {
        //tree showing the list of notebooks and the tags found in each one
        treeView = new JScrollPane();
        notebooksPanelLeft.add(treeView);
        createNodes();
    }

    // MODIFIES: this
    // EFFECTS: generates the nodes in the tree based on the information in the user's notebooks
    private void createNodes() {
        rootNode = new DefaultMutableTreeNode("My Notebooks");
        for (Notebook notebook:breadtree.getNotebooks()) {
            DefaultMutableTreeNode notebookNode = new DefaultMutableTreeNode(notebook.getName());
            rootNode.add(notebookNode);
            for (String tag:notebook.getAllTags()) {
                DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode(tag);
                notebookNode.add(tagNode);
            }
        }
        notebooksPanelLeft.remove(treeView);
        notebookTree = new JTree(rootNode);
        notebookTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        notebookTree.addTreeSelectionListener(this);
        treeView = new JScrollPane(notebookTree);
        notebooksPanelLeft.add(treeView, 0);
    }

    // REQUIRES: a node nodeName exists in the tree
    // MODIFIES: this
    // EFFECTS: returns a TreePath object representing the path to the node with the given name
    public TreePath getNotebookPathByNodeName(String nodeName) {
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if (child.getUserObject().equals(nodeName)) {
                return new TreePath(child.getPath());
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: sets up the notebook buttons "New", "Delete", and "Save"
    private void setupButtons() {
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(new TitledBorder("Notebook Options"));

        newNotebookButton = new JButton("New");
        newNotebookButton.addActionListener(this);
        newNotebookButton.setActionCommand("newNotebookButton");
        deleteNotebookButton = new JButton("Delete");
        deleteNotebookButton.addActionListener(this);
        deleteNotebookButton.setActionCommand("deleteNotebookButton");
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setActionCommand("saveButton");

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridy = 0;
        buttonPanel.add(newNotebookButton, c);

        c.gridy = 1;
        buttonPanel.add(deleteNotebookButton, c);

        c.gridy = 2;
        buttonPanel.add(saveButton, c);

        notebooksPanelLeft.add(buttonPanel);
    }

    // MODIFIES: this
    // EFFECTS: sets up the table on the right of the application representing a list of entries
    private void setupTable() {
        //table containing words, definitions, and tags of the current notebook
        notebookTableModel = new NotebookTableModel();
        notebookTable = new JTable(notebookTableModel);
        notebookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel = notebookTable.getSelectionModel();
        tableSelectionModel.addListSelectionListener(this);
        notebookTable.setSelectionModel(tableSelectionModel);
        tableView = new JScrollPane(notebookTable);
        notebooksPanelRight.add(tableView);
        notebookTableModel.updateDataFromNotebook(currentNotebook);
        notebookTableModel.fireTableDataChanged();
    }

    // MODIFIES: this
    // EFFECTS: listener method that detects when a selection is made on the tree
    // and changes the table display to reflect the selection made
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) notebookTree.getLastSelectedPathComponent();

        if (node == null) {
            //Nothing is selected.
            return;
        }

        Object nodeInfo = node.getUserObject();
        if (node.isRoot()) {
            return;
        } else if (((DefaultMutableTreeNode) node.getParent()).isRoot()) {
            String notebook = (String) nodeInfo;
            currentNotebook = breadtree.getNotebookByName(notebook);
            notebookTableModel.updateDataFromNotebook(currentNotebook);
            dynamicLabel.setText("Current notebook: " + notebook);
            isViewingNotebook = true;
        } else if (node.isLeaf()) {
            String tag = (String) nodeInfo;
            List<String> tagInList = new ArrayList<>();
            tagInList.add(tag);
            DefaultMutableTreeNode parentNotebookNode = (DefaultMutableTreeNode) node.getParent();
            Object parentNodeInfo = parentNotebookNode.getUserObject();
            Notebook parentNotebook = breadtree.getNotebookByName((String) parentNodeInfo);
            currentNotebook = parentNotebook;
            Notebook filteredParentNotebook = new Notebook("", parentNotebook.getEntriesTagged(tagInList));
            notebookTableModel.updateDataFromNotebook(filteredParentNotebook);
            dynamicLabel.setText("Viewing entries tagged \"" + tag + "\" in notebook \"" + parentNodeInfo
                    + "\". Entries cannot be edited in this view.");
            isViewingNotebook = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: to do...
    public void valueChanged(ListSelectionEvent e) {
        return;
    }

    // MODIFIES: this
    // EFFECTS: sets up the text entry field below the table
    private void setupEntryField() {
        //text field for creating and editing entries
        entryField = new JTextField();
        entryField.addActionListener(this);
        entryField.setActionCommand("entryField");
        entryField.setBorder(new LineBorder(Color.DARK_GRAY, 3, false));
        JLabel entryFieldLabel = new JLabel(" >> ");
        Box entryFieldBox = Box.createHorizontalBox();
        entryFieldBox.add(entryFieldLabel);
        entryFieldBox.add(entryField);
        entryFieldBox.setMaximumSize(new Dimension(800, 1));
        notebooksPanelRight.add(entryFieldBox);
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

    // Adapted from CPSC 210 JsonSerializationDemo
    // EFFECTS: saves breadtree to file
    private void saveBreadtree() {
        try {
            jsonWriter.open();
            jsonWriter.write(breadtree);
            jsonWriter.close();
            System.out.println("Saved to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: handles text entry and button presses
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("entryField")) {
            updateEntry();
        } else if (e.getActionCommand().equals("newNotebookButton")) {
            String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Enter a name for your new notebook:",
                    "New Notebook",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);

            if ((s != null) && (s.length() > 0)) {
                dynamicLabel.setText("Created new notebook \"" + s + "\" successfully.");
                breadtree.makeNotebook(s);
                createNodes();
                notebookTree.setSelectionPath(getNotebookPathByNodeName(s));
            }
        } else if (e.getActionCommand().equals("deleteNotebookButton")) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "Delete notebook \"" + currentNotebook.getName() + "\"?",
                    "",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                dynamicLabel.setText("Deleted notebook \"" + currentNotebook.getName() + "\" successfully.");
                breadtree.deleteNotebook(currentNotebook);
                currentNotebook = null;
                createNodes();
            }
        } else if (e.getActionCommand().equals("saveButton")) {
            dynamicLabel.setText("Save completed sucessfully.");
            saveBreadtree();
        }
    }

    // MODIFIES: this
    // EFFECTS: parses the text in the entry field and adds it as a new entry to the current notebook
    private void updateEntry() {
        String[] wordComponents = entryField.getText().split("\\s*,\\s*");
        List<String> tags = new ArrayList<>();
        for (int i = 2; i < wordComponents.length; i++) {
            tags.add(wordComponents[i]);
        }
        Entry entry = new Entry(wordComponents[0], wordComponents[1], new ArrayList<>());
        for (String tag:tags) {
            entry.addTag(tag);
        }
        currentNotebook.addEntry(entry);
        entryField.setText("");
        createNodes();
        notebookTableModel.updateDataFromNotebook(currentNotebook);
        dynamicLabel.setText("Added new entry \""
                + wordComponents[0]
                + "\" to notebook \""
                + currentNotebook.getName()
                + "\".");
    }

    public static void main(String[] args) {
        new BreadtreeUI();
    }
}
