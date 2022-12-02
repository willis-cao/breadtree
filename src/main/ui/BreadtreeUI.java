package ui;

import exceptions.EntryExistsException;
import exceptions.NotebookExistsException;
import model.*;
import model.Event;
import model.EventLog;
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
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Represents a graphical user interface for the Breadtree application
public class BreadtreeUI extends JFrame implements ActionListener, TreeSelectionListener {

    private static final String JSON_STORE = "./data/breadtree.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private Breadtree breadtree;

    private Notebook currentNotebook;
    private Entry currentEntry;
    private boolean modeEditEntry;

    JTabbedPane tabbedPane;
    JPanel notebooksPanelVertical;
    JPanel notebooksPanelHorizontal;
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

    // EFFECTS: constructs and initializes a GUI for the Breadtree application
    public BreadtreeUI() throws FileNotFoundException {
        super("Breadtree");

        // initialize persistence
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        // initialize notebooks
        breadtree = new Breadtree();
        loadBreadtree();
        currentNotebook = breadtree.getNotebooks().get(0); //placeholder notebook
        modeEditEntry = false;

        // begin setup of graphical components!
        setupTabbedPane();

        // panel operations
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setupEventLogOnClose();
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    // EFFECTS: prints out the event log in the console when the application is closed
    private void setupEventLogOnClose() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                for (Event next : EventLog.getInstance()) {
                    System.out.println(next.toString());
                }
                System.exit(0);
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: sets up the tabbed pane of the GUI
    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        add(tabbedPane);
        setupTabNotebooks();
    }

    // MODIFIES: this
    // EFFECTS: sets up the "Notebooks" tab of the GUI (currently the only tab!)
    public void setupTabNotebooks() {
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

        // set up the components of this tab
        setupDynamicLabel();
        setupTree();
        setupButtons();
        setupTable();
        setupEntryField();
    }

    // MODIFIES: this
    // EFFECTS: sets up the dynamic label at the bottom of the application that provides
    // the user with information about the current status of the application
    private void setupDynamicLabel() {
        dynamicLabel = new JLabel("");
        dynamicLabel.setText("Welcome to Breadtree!");
        Box dynamicLabelBox = Box.createHorizontalBox();
        dynamicLabelBox.add(dynamicLabel);
        dynamicLabelBox.add(Box.createHorizontalGlue());
        notebooksPanelVertical.add(dynamicLabelBox);
    }

    // MODIFIES: this
    // EFFECTS: sets up the tree at the left of the application that represents the user's
    // notebooks and the tags contained in each one
    private void setupTree() {
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
    // EFFECTS: listener method that detects when a selection is made on the tree
    // and changes the table display to reflect the selection made
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) notebookTree.getLastSelectedPathComponent();
        Object nodeInfo = node.getUserObject();
        if (node == null || node.isRoot()) {
            return;
        } else if (((DefaultMutableTreeNode) node.getParent()).isRoot()) {
            String notebook = (String) nodeInfo;
            currentNotebook = breadtree.getNotebookByName(notebook);
            notebookTableModel.updateDataFromNotebook(currentNotebook);
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
            dynamicLabel.setText("Viewing entries tagged \"" + tag + "\" in notebook \"" + parentNodeInfo
                    + "\". Entries cannot be edited in this view.");
        }
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
        notebookTableModel = new NotebookTableModel();
        notebookTable = new JTable(notebookTableModel);
        notebookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addTableListeners();
        tableView = new JScrollPane(notebookTable);
        notebooksPanelRight.add(tableView);
        notebookTableModel.updateDataFromNotebook(currentNotebook);
    }

    // MODIFIES: this
    // EFFECTS: adds selection and key event listeners to the table
    private void addTableListeners() {
        notebookTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (notebookTable.getSelectedRow() != -1) {
                    entrySelected(notebookTable.getValueAt(notebookTable.getSelectedRow(), 0).toString());
                }
            }
        });
        notebookTable.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    askDeleteEntry();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    // MODIFIES: this
    // EFFECTS: sets the current entry field to the selected entry, populates
    // the entry field with the contents of the entry, and enters entry editing mode
    private void entrySelected(String entryName) {
        Entry entryByName = currentNotebook.getEntryByWord(entryName);
        currentEntry = entryByName;
        enterModeEditEntry();
        entryField.setText(entryByName.getWord() + ", "
                + entryByName.getDefinition() + ", "
                + entryByName.tagsAsString());
    }

    // MODIFIES: this
    // EFFECTS: sets up the text entry field below the table
    // and adds action and key listeners
    private void setupEntryField() {
        //text field for creating and editing entries
        entryField = new JTextField();
        entryField.addActionListener(this);
        entryField.setActionCommand("entryField");
        entryField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    exitModeEditEntry();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
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
            askNewNotebook();
        } else if (e.getActionCommand().equals("deleteNotebookButton")) {
            askDeleteNotebook();
        } else if (e.getActionCommand().equals("saveButton")) {
            dynamicLabel.setText("Save completed sucessfully.");
            saveBreadtree();
        }
    }

    // EFFECTS: enters entry-editing mode
    public void enterModeEditEntry() {
        modeEditEntry = true;
        dynamicLabel.setText("Editing entry...");
        entryField.setBorder(new LineBorder(Color.YELLOW, 3, false));
    }

    // EFFECTS: exits entry-editing mode
    public void exitModeEditEntry() {
        modeEditEntry = false;
        entryField.setBorder(new LineBorder(Color.DARK_GRAY, 3, false));
    }

    // MODIFIES: this
    // EFFECTS: parses the text in the entry field and, depending on whether the entry field
    // is in entry-editing mode or not, either edits the currently selected entry
    // or creates a new entry
    private void updateEntry() {
        String[] wordComponents = entryField.getText().split("\\s*,\\s*");
        if (wordComponents.length < 2) {
            JOptionPane.showMessageDialog(this,
                    "Entry is incomplete!");
        } else {
            List<String> tags = new ArrayList<>();
            for (int i = 2; i < wordComponents.length; i++) {
                tags.add(wordComponents[i]);
            }
            if (modeEditEntry) {
                editEntry(currentEntry, wordComponents, tags);
            } else {
                newEntry(wordComponents, tags);
            }

            entryField.setText("");
            createNodes();
            notebookTableModel.updateDataFromNotebook(currentNotebook);
        }
    }

    // MODIFIES: currentEntry, currentNotebook, this
    // EFFECTS: updates the current entry based on the given arguments
    private void editEntry(Entry entry, String[] entryString, List<String> tags) {
        entry.setWord(entryString[0]);
        entry.setDefinition(entryString[1]);
        entry.setTags(tags);
        dynamicLabel.setText("Edited entry \""
                + entryString[0]
                + "\" in notebook \""
                + currentNotebook.getName()
                + "\".");
        exitModeEditEntry();
    }

    // MODIFIES: currentNotebook, this
    // EFFECTS: adds a new entry to the current notebook containing the given arguments
    private void newEntry(String[] entryString, List<String> tags) {
        Entry entry = new Entry(entryString[0], entryString[1], new ArrayList<>());
        for (String tag:tags) {
            entry.addTag(tag);
        }
        try {
            currentNotebook.addEntry(entry);
            dynamicLabel.setText("Added new entry \""
                    + entryString[0]
                    + "\" to notebook \""
                    + currentNotebook.getName()
                    + "\".");
        } catch (EntryExistsException e) {
            JOptionPane.showMessageDialog(this,
                    "Entry \"" + entryString[0] + "\" already exists!");
        }
    }

    // MODIFIES: currentEntry, currentNotebook, this
    // EFFECTS: brings up a dialog prompt asking the user if the currently
    // selected entry should be deleted and if yes, deletes the selected entry
    private void askDeleteEntry() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Delete entry \""
                        + currentEntry.getWord()
                        + "\" from notebook \""
                        + currentNotebook.getName() + "\"?",
                "",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            dynamicLabel.setText("Deleted entry \""
                    + currentEntry.getWord() + "\" from notebook \""
                    + currentNotebook.getName() + "\" successfully.");
            currentNotebook.deleteEntry(currentEntry);
            exitModeEditEntry();
            currentEntry = null;
            createNodes();
            notebookTableModel.updateDataFromNotebook(currentNotebook);
        }
    }

    // MODIFIES: breadtree, currentNotebook, this
    // EFFECTS: brings up a dialog prompt asking the user to input a notebook name
    // and creates a new notebook with the given name
    private void askNewNotebook() {
        String s = (String)JOptionPane.showInputDialog(this, "Enter a name for your new notebook:",
                "New Notebook", JOptionPane.PLAIN_MESSAGE, null, null, null);

        if ((s != null) && (s.length() > 0)) {
            dynamicLabel.setText("Created new notebook \"" + s + "\" successfully.");
            try {
                breadtree.makeNotebook(s);
                createNodes();
                notebookTree.setSelectionPath(getNotebookPathByNodeName(s));
                currentNotebook = breadtree.getNotebookByName(s);
                notebookTableModel.updateDataFromNotebook(currentNotebook);
            } catch (NotebookExistsException e) {
                JOptionPane.showMessageDialog(this, "Notebook \"" + s + "\" already exists!");
            }
        }
    }

    // MODIFIES: breadtree, currentNotebook, this
    // EFFECTS: brings up a dialog prompt confirming if the user would like to delete the selected notebook
    // and if yes, deletes the selected notebook
    private void askDeleteNotebook() {
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
    }
}
