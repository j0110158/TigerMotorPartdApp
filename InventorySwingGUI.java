import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import javax.swing.JComponent;
import javax.swing.AbstractAction;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.plaf.basic.*;

public class InventorySwingGUI extends JFrame implements ActionListener {
    private InventoryMgt inventoryManager;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTree categoryTree;
    private DefaultTreeModel categoryTreeModel;
    private JTextField searchField;
    private JTextArea statusTextArea;
    private JTextArea restockTextArea;
    private JScrollPane restockScrollPane;

    public InventorySwingGUI() {
        // Initialize inventory manager
        inventoryManager = new InventoryMgt();

        // Set up the main window
        setTitle("TIGER MOTORHUB APP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900); // Default size 1600x900
        setMinimumSize(new Dimension(1024, 600));
        // Standard Windows controls (minimize, maximize, close) are default for JFrame

        // Set look and feel to system default for Aero feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main layout
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Create split pane for category tree and item table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left side - Category tree
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(null);
        Color bg = new Color(240, 240, 240);
        categoryPanel.setBackground(bg);
        categoryTree = new JTree();
        categoryTree.setShowsRootHandles(true);
        categoryTree.setRootVisible(true);
        categoryTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();
            if (node != null && !node.isRoot()) {
                String categoryName = node.getUserObject().toString();
                filterItemsByCategory(categoryName);
            } else if (node != null && node.isRoot()) {
                updateItemTable();
            }
        });
        JScrollPane categoryScroll = new JScrollPane(categoryTree);
        categoryScroll.setBorder(null);
        categoryScroll.setBackground(null);
        JSplitPane categorySplitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            categoryScroll,
            restockScrollPane
        );
        categorySplitPane.setResizeWeight(0.7); // 70% for tree, 30% for alerts (adjust as needed)
        categorySplitPane.setDividerSize(10);    // Thicker divider for easier dragging
        categorySplitPane.setBackground(null);
        categorySplitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(238, 238, 240));
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paint(g);
                    }
                };
            }
        });
        categoryPanel.setLayout(new BorderLayout());
        categoryPanel.add(categorySplitPane, BorderLayout.CENTER);

        // Right side - Item table and search
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createTitledBorder("Inventory Items"));
        itemPanel.setBackground(bg);

        // Search bar with improved layout
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(20);
        searchField.setActionCommand("Search");
        searchField.setToolTipText("Enter model number, name, or category to search");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton searchButton = new JButton("Search");
        searchButton.setToolTipText("Search inventory items");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear search and show all items");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // Add action listeners for search
        searchField.addActionListener(this);
        searchButton.addActionListener(this);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            updateItemTable();
        });

        // Item table with improved appearance
        itemTableModel = new DefaultTableModel(new Object[]{"Model Number", "Model Name", "Price", "Quantity", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemTable = new JTable(itemTableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.setAutoCreateRowSorter(true);

        // Set column widths
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Add right-click menu for table
        JPopupMenu tablePopupMenu = new JPopupMenu();
        JMenuItem editMenuItem = new JMenuItem("Edit Item");
        JMenuItem removeMenuItem = new JMenuItem("Remove Item");
        tablePopupMenu.add(editMenuItem);
        tablePopupMenu.add(removeMenuItem);

        editMenuItem.addActionListener(e -> editSelectedItem());
        removeMenuItem.addActionListener(e -> removeSelectedItem());

        itemTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = itemTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        itemTable.setRowSelectionInterval(row, row);
                        tablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // Add keyboard shortcuts for table
        itemTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removeItem");
        itemTable.getActionMap().put("removeItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });

        itemTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "editItem");
        itemTable.getActionMap().put("editItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedItem();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(itemTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 400));

        itemPanel.add(searchPanel, BorderLayout.NORTH);
        itemPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add both sides to split pane
        splitPane.setLeftComponent(categoryPanel);
        splitPane.setRightComponent(itemPanel);
        splitPane.setDividerLocation(250);

        add(splitPane, BorderLayout.CENTER);

        // Status bar with improved appearance (scrollable alert area)
        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        statusTextArea.setLineWrap(true);
        statusTextArea.setWrapStyleWord(true);
        statusTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
        statusScrollPane.setPreferredSize(new Dimension(getWidth(), 60)); // Fixed height
        add(statusScrollPane, BorderLayout.SOUTH);

        // Bottom panel for restock notification (bottom right)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 90)); // Match the new height
        restockTextArea = new JTextArea();
        restockTextArea.setEditable(false);
        restockTextArea.setLineWrap(true);
        restockTextArea.setWrapStyleWord(true);
        restockTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        restockTextArea.setBackground(bg);
        restockTextArea.setBorder(BorderFactory.createTitledBorder("Restock Alerts"));

        restockScrollPane = new JScrollPane(restockTextArea);
        restockScrollPane.setPreferredSize(new Dimension(categoryPanel.getWidth(), 250)); // Even larger height
        categoryPanel.add(restockScrollPane, BorderLayout.SOUTH);

        // Set window icon (placeholder - you'll need to provide the actual icon)
        try {
            // setIconImage(new ImageIcon("icons/app.png").getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        // Load initial data and update UI
        inventoryManager.importData(InventoryMgt.DATA_FILE); // Load existing data
        updateCategoryTree();
        updateItemTable();

        // For the whole window
        Color mainBg = new Color(100, 100, 100);
        Color cardBg = new Color(100, 100, 100); 

        // getContentPane().setBackground(mainBg);
        // categoryPanel.setBackground(mainBg);
        // itemPanel.setBackground(mainBg);
        // restockTextArea.setBackground(cardBg);
        // splitPane.setBackground(new Color(49, 52, 99));
        // splitPane.setUI(...);

        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem importItem = new JMenuItem("Import Data"); // Add icon later
        importItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        importItem.setToolTipText("Import inventory data from a CSV file");

        JMenuItem exportItem = new JMenuItem("Export Data"); // Add icon later
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        exportItem.setToolTipText("Export inventory data to a CSV file");

        JMenuItem exitItem = new JMenuItem("Exit"); // Add icon later
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
        exitItem.setToolTipText("Exit the application");

        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        JMenuItem addItem = new JMenuItem("Add Item"); // Add icon later
        addItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        addItem.setToolTipText("Add a new item to the inventory");

        JMenuItem removeItem = new JMenuItem("Remove Item"); // Add icon later
        removeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        removeItem.setToolTipText("Remove the selected item from the inventory");

        JMenuItem editItem = new JMenuItem("Edit Item"); // Add icon later
        editItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        editItem.setToolTipText("Edit the selected item's details");

        editMenu.add(addItem);
        editMenu.add(removeItem);
        editMenu.add(editItem);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenuItem refreshItem = new JMenuItem("Refresh"); // Add icon later
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.setToolTipText("Refresh the inventory view");

        JMenuItem transactionLogItem = new JMenuItem("Transaction Log"); // Add icon later
        transactionLogItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        transactionLogItem.setToolTipText("View the transaction history");

        viewMenu.add(refreshItem);
        viewMenu.add(transactionLogItem);

        // Add action listeners for all menu items
        importItem.addActionListener(this);
        exportItem.addActionListener(this);
        exitItem.addActionListener(this);
        addItem.addActionListener(this);
        removeItem.addActionListener(this);
        editItem.addActionListener(this);
        refreshItem.addActionListener(this);
        transactionLogItem.addActionListener(this);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        return menuBar;
    }

    // Method to handle button clicks and other actions
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Search":
                filterItems();
                break;
            case "Add Item":
                showAddItemDialog();
                break;
            case "Remove Item":
                removeSelectedItem();
                break;
            case "Edit Item":
                editSelectedItem();
                break;
            case "Refresh":
                updateCategoryTree();
                updateItemTable();
                statusTextArea.setText("Inventory refreshed.");
                break;
            case "Import Data":
                importData();
                break;
            case "Export Data":
                exportData();
                break;
            case "Transaction Log":
                showTransactionLogDialog();
                break;
            case "Exit":
                if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
        }
    }

    // Method to filter items in the table based on search field text
    private void filterItems() {
        String searchTerm = searchField.getText().toLowerCase();
        itemTableModel.setRowCount(0); // Clear current table data

        List<Item> allItems = inventoryManager.getInventoryItems();
        for (Item item : allItems) {
            // Check if model number, name, or category contains the search term
            if (item.getModelNumber().toLowerCase().contains(searchTerm) ||
                item.getModelName().toLowerCase().contains(searchTerm) ||
                item.getItemCategory().toLowerCase().contains(searchTerm)) {
                itemTableModel.addRow(new Object[]{item.getModelNumber(), item.getModelName(), item.getModelPrice(), item.getItemQuantity(), item.getItemCategory()});
            }
        }
        statusTextArea.setText("Filtered items by: " + searchTerm);
    }

    // Method to filter items in the table by category
    private void filterItemsByCategory(String categoryName) {
        itemTableModel.setRowCount(0); // Clear current table data

        if (categoryName.equals("All Categories")) {
            updateItemTable(); // Show all items if "All Categories" is selected
            return;
        }

        List<Item> allItems = inventoryManager.getInventoryItems();
        for (Item item : allItems) {
            if (item.getItemCategory().equals(categoryName)) {
                itemTableModel.addRow(new Object[]{item.getModelNumber(), item.getModelName(), item.getModelPrice(), item.getItemQuantity(), item.getItemCategory()});
            }
        }
        statusTextArea.setText("Filtered items by category: " + categoryName);
    }

    // Method to remove the selected item from the table and backend
    private void removeSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the model number from the selected row (assuming Model Number is the first column)
        String modelNumber = (String) itemTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove item with Model Number: " + modelNumber + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Find the item to log the transaction before removing
                Item itemToRemove = inventoryManager.searchItem(modelNumber); // Assuming searchItem finds by model number
                if (itemToRemove != null) {
                     inventoryManager.logTransaction("REMOVE", itemToRemove.getModelName(), itemToRemove.getModelNumber(), itemToRemove.getItemQuantity());
                }

                inventoryManager.removeItemByNumber(modelNumber);
                inventoryManager.saveData(); // Save data after removing

                updateItemTable(); // Refresh table
                updateCategoryTree(); // Refresh tree
                statusTextArea.setText("Item removed successfully: " + modelNumber);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error removing item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Method to edit the selected item in the table and backend
    private void editSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get current item details from the selected row
        String currentModelNumber = (String) itemTableModel.getValueAt(selectedRow, 0);
        String currentModelName = (String) itemTableModel.getValueAt(selectedRow, 1);
        double currentModelPrice = (double) itemTableModel.getValueAt(selectedRow, 2);
        int currentItemQuantity = (int) itemTableModel.getValueAt(selectedRow, 3);
        String currentItemCategory = (String) itemTableModel.getValueAt(selectedRow, 4);

        // Find the actual Item object in the inventoryManager
        Item itemToEdit = inventoryManager.searchItem(currentModelNumber); // Assuming searchItem finds by model number
        if (itemToEdit == null) {
             JOptionPane.showMessageDialog(this, "Error: Could not find the selected item in the inventory.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        JDialog editItemDialog = new JDialog(this, "Edit Item", true);
        editItemDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField modelNumberField = new JTextField(currentModelNumber, 20);
        modelNumberField.setEnabled(false); // Model number is usually not editable
        JTextField modelNameField = new JTextField(currentModelName, 20);
        JTextField modelPriceField = new JTextField(String.valueOf(currentModelPrice), 20);
        JTextField itemQuantityField = new JTextField(String.valueOf(currentItemQuantity), 20);
        itemQuantityField.setEnabled(false); // Quantity is fixed at 1 as per logic
        JTextField itemCategoryField = new JTextField(currentItemCategory, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        editItemDialog.add(new JLabel("Model Number:"), gbc);
        gbc.gridy++;
        editItemDialog.add(new JLabel("Model Name:"), gbc);
        gbc.gridy++;
        editItemDialog.add(new JLabel("Model Price:"), gbc);
        gbc.gridy++;
        editItemDialog.add(new JLabel("Item Quantity:"), gbc);
        gbc.gridy++;
        editItemDialog.add(new JLabel("Item Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        editItemDialog.add(modelNumberField, gbc);
        gbc.gridy++;
        editItemDialog.add(modelNameField, gbc);
        gbc.gridy++;
        editItemDialog.add(modelPriceField, gbc);
        gbc.gridy++;
        editItemDialog.add(itemQuantityField, gbc);
        gbc.gridy++;
        editItemDialog.add(itemCategoryField, gbc);

        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        editItemDialog.add(buttonPanel, gbc);

        // Add action listeners for dialog buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get updated data from fields
                String updatedModelName = modelNameField.getText().trim();
                String updatedModelPriceStr = modelPriceField.getText().trim();
                String updatedItemCategory = itemCategoryField.getText().trim();

                // Basic validation
                if (updatedModelName.isEmpty() || updatedModelPriceStr.isEmpty() || updatedItemCategory.isEmpty()) {
                    JOptionPane.showMessageDialog(editItemDialog, "All fields except Model Number and Quantity are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double updatedModelPrice = Double.parseDouble(updatedModelPriceStr);
                    int updatedItemQuantity = 1; // Still fixed at 1

                    // Check if updated category exists, prompt to create if not
                     Category existingCategory = inventoryManager.findCategoryByName(updatedItemCategory);
                    if (existingCategory == null) {
                        int createCategoryResponse = JOptionPane.showConfirmDialog(editItemDialog, "Category \'" + updatedItemCategory + "\' not found. Do you want to create it?", "Category Not Found", JOptionPane.YES_NO_OPTION);
                        if (createCategoryResponse == JOptionPane.YES_OPTION) {
                            inventoryManager.addCategory(updatedItemCategory, 0); // Initial quantity 0
                            statusTextArea.setText("Category \'" + updatedItemCategory + "\' created.");
                        } else {
                            statusTextArea.setText("Item not updated: Category not created.");
                            editItemDialog.dispose();
                            return;
                        }
                    }

                    // Update the item details in the backend
                    // Note: Depending on your InventoryMgt implementation, you might need a specific update method
                    // For now, I'll assume you might remove and re-add, or the Item object itself is mutable and updated by reference
                    // A more robust approach in InventoryMgt would be an updateItem(modelNumber, newDetails) method.
                    // For this sample, let's directly update the found item object's properties if mutable:
                    itemToEdit.setModelName(updatedModelName);
                    itemToEdit.setModelPrice(updatedModelPrice);
                    itemToEdit.setItemCategory(updatedItemCategory);
                    // Assuming ItemQuantity is not editable as per logic

                    inventoryManager.saveData(); // Save data after editing

                    updateItemTable(); // Refresh table
                    updateCategoryTree(); // Refresh tree
                    statusTextArea.setText("Item updated successfully: " + itemToEdit.getModelName());
                    editItemDialog.dispose(); // Close dialog

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editItemDialog, "Invalid price format.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editItemDialog, "Error updating item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editItemDialog.dispose(); // Close dialog without saving
            }
        });

        editItemDialog.pack();
        editItemDialog.setLocationRelativeTo(this);
        editItemDialog.setVisible(true);
    }

    // Method to show the Add Item dialog
    private void showAddItemDialog() {
        JDialog addItemDialog = new JDialog(this, "Add New Item", true); // true makes it modal
        addItemDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        JTextField modelNumberField = new JTextField(20);
        JTextField modelNameField = new JTextField(20);
        JTextField modelPriceField = new JTextField(20);
        JTextField itemQuantityField = new JTextField("1"); // Default to 1 as per logic
        itemQuantityField.setEnabled(false); // Disable as quantity is fixed at 1
        JTextField itemCategoryField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addItemDialog.add(new JLabel("Model Number:"), gbc);
        gbc.gridy++;
        addItemDialog.add(new JLabel("Model Name:"), gbc);
        gbc.gridy++;
        addItemDialog.add(new JLabel("Model Price:"), gbc);
        gbc.gridy++;
        addItemDialog.add(new JLabel("Item Quantity:"), gbc);
        gbc.gridy++;
        addItemDialog.add(new JLabel("Item Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        addItemDialog.add(modelNumberField, gbc);
        gbc.gridy++;
        addItemDialog.add(modelNameField, gbc);
        gbc.gridy++;
        addItemDialog.add(modelPriceField, gbc);
        gbc.gridy++;
        addItemDialog.add(itemQuantityField, gbc);
        gbc.gridy++;
        addItemDialog.add(itemCategoryField, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // Span across two columns
        addItemDialog.add(buttonPanel, gbc);

        // Add action listeners for dialog buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get data from fields
                String modelNumber = modelNumberField.getText().trim();
                String modelName = modelNameField.getText().trim();
                String modelPriceStr = modelPriceField.getText().trim();
                String itemCategory = itemCategoryField.getText().trim();

                // Basic validation
                if (modelNumber.isEmpty() || modelName.isEmpty() || modelPriceStr.isEmpty() || itemCategory.isEmpty()) {
                    JOptionPane.showMessageDialog(addItemDialog, "All fields except Quantity are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double modelPrice = Double.parseDouble(modelPriceStr);
                    int itemQuantity = 1; // Fixed quantity as per logic

                    // Check if category exists, prompt to create if not
                    Category existingCategory = inventoryManager.findCategoryByName(itemCategory);
                    if (existingCategory == null) {
                        int createCategoryResponse = JOptionPane.showConfirmDialog(addItemDialog, "Category \'" + itemCategory + "\' not found. Do you want to create it?", "Category Not Found", JOptionPane.YES_NO_OPTION);
                        if (createCategoryResponse == JOptionPane.YES_OPTION) {
                            inventoryManager.addCategory(itemCategory, 0); // Initial quantity 0, will be updated by item
                            statusTextArea.setText("Category \'" + itemCategory + "\' created.");
                        } else {
                            statusTextArea.setText("Item not added: Category not created.");
                            addItemDialog.dispose(); // Close dialog
                            return;
                        }
                    }

                    // Create and add the item
                    Item newItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                    inventoryManager.addItem(newItem);
                    inventoryManager.logTransaction("ADD", modelName, modelNumber, itemQuantity);
                    inventoryManager.saveData(); // Save data after adding

                    updateItemTable(); // Refresh table
                    updateCategoryTree(); // Refresh tree
                    statusTextArea.setText("Item added successfully: " + modelName);
                    addItemDialog.dispose(); // Close dialog

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addItemDialog, "Invalid price format.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addItemDialog, "Error adding item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Print stack trace for debugging
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemDialog.dispose(); // Close dialog without saving
            }
        });

        addItemDialog.pack(); // Size the dialog based on its components
        addItemDialog.setLocationRelativeTo(this); // Center dialog relative to the main frame
        addItemDialog.setVisible(true);
    }

    // Method to show the transaction log dialog
    private void showTransactionLogDialog() {
        JDialog logDialog = new JDialog(this, "Transaction Log", true);
        logDialog.setLayout(new BorderLayout());
        logDialog.setSize(800, 600);
        logDialog.setLocationRelativeTo(this);

        // Create table model for transaction log
        DefaultTableModel logTableModel = new DefaultTableModel(
            new Object[]{"Timestamp", "Action", "Model Name", "Model Number", "Quantity", "Category"}, 0);
        JTable logTable = new JTable(logTableModel);
        JScrollPane scrollPane = new JScrollPane(logTable);

        // Get transaction logs and populate table
        List<String> logs = inventoryManager.readTransactionLogsFromFile();
        for (String logEntry : logs) {
            String[] parts = logEntry.split(",");
            if (parts.length >= 5) {
                logTableModel.addRow(new Object[]{
                    parts[0], // Timestamp
                    parts[1], // Action
                    parts[2], // Model Name
                    parts[3], // Model Number
                    parts[4], // Quantity
                    parts.length > 5 ? parts[5] : "" // Category (optional)
                });
            }
        }

        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> logDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        logDialog.add(scrollPane, BorderLayout.CENTER);
        logDialog.add(buttonPanel, BorderLayout.SOUTH);
        logDialog.setVisible(true);
    }

    // Method to handle import data
    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
                inventoryManager.importData(selectedFile);
                updateCategoryTree();
                updateItemTable();
                statusTextArea.setText("Data imported successfully from: " + selectedFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error importing data: " + ex.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to handle export data
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
                if (!selectedFile.toLowerCase().endsWith(".csv")) {
                    selectedFile += ".csv";
                }
                inventoryManager.exportData(selectedFile);
                statusTextArea.setText("Data exported successfully to: " + selectedFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting data: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to update the item table
    public void updateItemTable() {
        // Clear existing data
        itemTableModel.setRowCount(0);
        // Get data from inventoryManager and add to table model
        List<Item> items = inventoryManager.getInventoryItems();
        // Category-based restock alerts
        Map<String, Integer> restockCount = new HashMap<>();
        for (Item item : items) {
            itemTableModel.addRow(new Object[]{item.getModelNumber(), item.getModelName(), item.getModelPrice(), item.getItemQuantity(), item.getItemCategory()});
            if (item.getItemQuantity() < 5) { // Restock threshold
                String category = item.getItemCategory();
                restockCount.put(category, restockCount.getOrDefault(category, 0) + 1);
            }
        }
        StringBuilder restockAlerts = new StringBuilder();
        for (Map.Entry<String, Integer> entry : restockCount.entrySet()) {
            restockAlerts.append("Restock needed in category: ")
                         .append(entry.getKey())
                         .append(" (" + entry.getValue() + " item(s) low)\n");
        }
        if (restockAlerts.length() > 0) {
            restockTextArea.setText(restockAlerts.toString());
        } else {
            restockTextArea.setText("");
        }
    }

    // Method to update the category tree
    public void updateCategoryTree() {
         DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Categories");
         // Get categories from InventoryMgt
        List<Category> categories = inventoryManager.getItemCategories();

        // Add categories to the tree
        for (Category category : categories) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category.getCategoryName());
            root.add(categoryNode);
        }

         categoryTreeModel = new DefaultTreeModel(root);
         categoryTree.setModel(categoryTreeModel);

        // Expand the root node
        categoryTree.expandRow(0);
    }

    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InventorySwingGUI gui = new InventorySwingGUI();
                // Initial data load and display is handled within the InventorySwingGUI constructor
            }
        });
    }
} 