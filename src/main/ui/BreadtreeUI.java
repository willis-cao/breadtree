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
import java.io.FileNotFoundException;
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
    DefaultMutableTreeNode rootNode;
    JTree notebookTree;
    JScrollPane treeView;
    JTable notebookTable;
    JScrollPane tableView;
    NotebookTableModel notebookTableModel;
    JLabel dynamicLabel;
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
//        notebooksPanelLeft.setBackground(Color.BLUE);
//        notebooksPanelRight.setBackground(Color.RED);
    }

    public void setupTabNotebooks() {
        //TAB 1: NOTEBOOKS TAB
        notebooksPanel = new JPanel();
        notebooksPanel.setLayout(new BoxLayout(notebooksPanel, BoxLayout.LINE_AXIS));
        tabbedPane.addTab("Notebooks", notebooksPanel);
        notebooksPanelLeft = new JPanel();
        notebooksPanelLeft.setLayout(new BoxLayout(notebooksPanelLeft, BoxLayout.PAGE_AXIS));
        notebooksPanelLeft.setMinimumSize(new Dimension(220, 500));
        notebooksPanelRight = new JPanel();
        notebooksPanelRight.setLayout(new BoxLayout(notebooksPanelRight, BoxLayout.PAGE_AXIS));

        notebooksPanel.add(notebooksPanelLeft);
        notebooksPanel.add(notebooksPanelRight);

        setupTree();
        setupTable();
        setupDynamicLabel();
        setupEntryField();
        setupButtons();
    }

    private void setupTree() {
        //tree showing the list of notebooks and the tags found in each one
        treeView = new JScrollPane();
        notebooksPanelLeft.add(treeView);
        createNodes();
    }

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

    private void setupTable() {
        //table containing words, definitions, and tags of the current notebook
        notebookTableModel = new NotebookTableModel();
        notebookTable = new JTable(notebookTableModel);
        tableView = new JScrollPane(notebookTable);
        notebooksPanelRight.add(tableView);
        notebookTableModel.updateDataFromNotebook(currentNotebook);
        notebookTableModel.fireTableDataChanged();
    }

    private void setupDynamicLabel() {
        dynamicLabel = new JLabel("");
        dynamicLabel.setText("Test text...");
        notebooksPanelRight.add(dynamicLabel);
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
        buttonPanel.setBorder(new TitledBorder("Notebook Options"));
        buttonPanel.setMaximumSize(new Dimension(220, 200));

        newNotebookButton = new JButton("New");
        newNotebookButton.addActionListener(this);
        newNotebookButton.setActionCommand("newNotebookButton");
        deleteNotebookButton = new JButton("Delete");
        deleteNotebookButton.addActionListener(this);
        deleteNotebookButton.setActionCommand("deleteNotebookButton");
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setActionCommand("saveButton");

        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.weightx = 0.5;
        c1.gridy = 0;
        buttonPanel.add(newNotebookButton, c1);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.weightx = 0.5;
        c2.gridy = 1;
        buttonPanel.add(deleteNotebookButton, c2);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.weightx = 0.5;
        c3.gridy = 2;

        buttonPanel.add(saveButton, c3);

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
            notebookTableModel.fireTableDataChanged();
            dynamicLabel.setText("Current notebook: " + notebook);
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
            notebookTableModel.fireTableDataChanged();
            dynamicLabel.setText("Viewing entries tagged \"" + tag + "\" in notebook \"" + parentNodeInfo
                    + "\". Entries cannot be edited in this view.");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("entryField")) {
            System.out.println(entryField.getText());
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
                breadtree.makeNotebook(s);
                createNodes();
            }
        } else if (e.getActionCommand().equals("deleteNotebookButton")) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "Delete notebook \"" + currentNotebook.getName() + "\"?",
                    "",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                breadtree.deleteNotebook(currentNotebook);
                createNodes();
            }
        } else if (e.getActionCommand().equals("saveButton")) {
            saveBreadtree();
            dynamicLabel.setText("Save completed sucessfully.");
        }
    }

    public static void main(String[] args) {
        new BreadtreeUI();
    }
}
