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
        //notebooksPanelLeft.setMinimumSize(new Dimension(220, 500));
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

    private void setupDynamicLabel() {
        dynamicLabel = new JLabel("");
        dynamicLabel.setText("Test text...");
        Box dynamicLabelBox = Box.createHorizontalBox();
        dynamicLabelBox.add(dynamicLabel);
        dynamicLabelBox.add(Box.createHorizontalGlue());
        notebooksPanelVertical.add(dynamicLabelBox);
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

    public TreePath getNotebookPathByNodeName(String nodeName) {
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if (child.getUserObject().equals(nodeName)) {
                return new TreePath(child.getPath());
            }
        }
        return null;
    }

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

    public void valueChanged(ListSelectionEvent e) {
        return;
    }

    private void setupEntryField() {
        //text field for creating and editing entries
        entryField = new JTextField();
        entryField.addActionListener(this);
        entryField.setActionCommand("entryField");
        JLabel entryFieldLabel = new JLabel(">");
        Box entryFieldBox = Box.createHorizontalBox();
        entryFieldBox.setBorder(new LineBorder(Color.DARK_GRAY, 3, true));
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

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("entryField")) {
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

    public static void main(String[] args) {
        new BreadtreeUI();
    }
}
