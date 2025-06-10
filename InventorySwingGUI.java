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
import java.io.File; // Import File class

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
    private JLabel totalWorthLabel; // New JLabel for cumulative worth
    private JButton searchButton; // Declare as class member
    private JButton clearButton; // Declare as class member

    public InventorySwingGUI() {
        // Initialize inventory manager
        inventoryManager = new InventoryMgt();

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

        // Define a dark color palette for UIManager
        Color darkBackground = new Color(45, 45, 48); // Main background
        Color darkPanel = new Color(60, 63, 65); // Panel backgrounds
        Color darkText = new Color(240, 240, 240); // Light text color
        Color darkControl = new Color(70, 73, 75); // General control background
        Color darkHighlight = new Color(75, 110, 175); // Highlight/selection color
        Color darkBorder = new Color(90, 93, 95); // Border color
        Color darkInput = new Color(40, 40, 40); // Input field background

        // Set UIManager defaults for a consistent dark theme
        UIManager.put("control", darkPanel); // General background for controls
        UIManager.put("info", darkPanel); // Info background (tooltips etc.)
        UIManager.put("nimbusBase", darkHighlight); // Primary interactive elements
        UIManager.put("nimbusBlueGrey", darkPanel); // Secondary background
        UIManager.put("text", darkText); // Default text color
        UIManager.put("nimbusLightBackground", darkBackground); // Overall background
        UIManager.put("nimbusFocus", darkHighlight); // Focus color

        // Component-specific colors
        UIManager.put("Panel.background", darkBackground);
        UIManager.put("Label.foreground", darkText);
        UIManager.put("TitledBorder.titleColor", darkText);

        // Buttons
        UIManager.put("Button.background", darkControl);
        UIManager.put("Button.foreground", darkText);
        UIManager.put("Button.light", darkBorder);
        UIManager.put("Button.highlight", darkControl);

        // Text fields, areas, panes
        UIManager.put("TextField.background", darkInput);
        UIManager.put("TextField.foreground", darkText);
        UIManager.put("TextField.caretForeground", darkText);
        UIManager.put("TextArea.background", darkInput);
        UIManager.put("TextArea.foreground", darkText);
        UIManager.put("TextPane.background", darkInput);
        UIManager.put("TextPane.foreground", darkText);

        // Tables
        UIManager.put("Table.background", darkInput);
        UIManager.put("Table.foreground", darkText);
        UIManager.put("Table.selectionBackground", darkHighlight);
        UIManager.put("Table.selectionForeground", darkText);
        UIManager.put("Table.gridColor", darkBorder);

        // Table Headers
        UIManager.put("TableHeader.background", darkControl);
        UIManager.put("TableHeader.foreground", darkText);

        // Trees
        UIManager.put("Tree.background", darkPanel);
        UIManager.put("Tree.foreground", darkText);
        UIManager.put("Tree.selectionBackground", darkHighlight);
        UIManager.put("Tree.selectionForeground", darkText);
        UIManager.put("Tree.textBackground", darkPanel); // For text background when not selected
        UIManager.put("Tree.textForeground", darkText);
        UIManager.put("Tree.hash", darkBorder); // Lines connecting nodes

        // Menus
        UIManager.put("MenuBar.background", darkControl);
        UIManager.put("MenuBar.foreground", darkText);
        UIManager.put("Menu.background", darkControl);
        UIManager.put("Menu.foreground", darkText);
        UIManager.put("MenuItem.background", darkControl);
        UIManager.put("MenuItem.foreground", darkText);
        UIManager.put("PopupMenu.background", darkControl);
        UIManager.put("MenuItem.acceleratorForeground", Color.BLACK); // Set accelerator text color to black

        // Scroll Panes
        UIManager.put("ScrollPane.background", darkBackground);

        // Split Pane Divider
        UIManager.put("SplitPane.background", darkBorder);
        UIManager.put("SplitPaneDivider.draggingColor", darkHighlight);

        // Dialogs
        UIManager.put("OptionPane.background", darkPanel);
        UIManager.put("OptionPane.messageForeground", darkText);
        UIManager.put("OptionPane.buttonAreaBackground", darkPanel);

        // Set up the main window
        setTitle("TIGER MOTORHUB APP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900); // Default size 1600x900
        setMinimumSize(new Dimension(1024, 600));
        // Standard Windows controls (minimize, maximize, close) are default for JFrame

        // Create the main layout
        setLayout(new BorderLayout());
        // getContentPane().setBackground(mainBg); // This can be removed or left as is if Nimbus handles it

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Create split pane for category tree and item table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left side - Category tree
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(null);
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
                        g.setColor(darkBorder); // Set divider color
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

        // Search bar with improved layout
        JPanel searchPanel = new JPanel(new BorderLayout()); // Use BorderLayout for searchPanel

        // Sub-panel for search input and buttons (left side)
        JPanel leftControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(20);
        searchField.setActionCommand("Search");
        searchField.setToolTipText("Enter model number, name, or category to search");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchButton = new JButton("Search"); // Initialize here
        searchButton.setToolTipText("Search inventory items");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        clearButton = new JButton("Clear"); // Initialize here
        clearButton.setToolTipText("Clear search and show all items");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel searchLabel = new JLabel("Search:");
        leftControlsPanel.add(searchLabel);
        leftControlsPanel.add(searchField);
        leftControlsPanel.add(searchButton);
        leftControlsPanel.add(clearButton);

        searchPanel.add(leftControlsPanel, BorderLayout.WEST); // Add left controls to the west

        // Add JLabel for cumulative worth to the search panel (right side)
        totalWorthLabel = new JLabel("Total Inventory Worth: Php0.00"); // Initial text
        totalWorthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalWorthLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Add some padding on right side
        searchPanel.add(totalWorthLabel, BorderLayout.EAST); // Directly add total worth to the east

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
        itemTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Increased font size
        itemTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Increased header font size

        // Set column widths
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Model Number
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Model Name
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Price
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Quantity
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Category

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
        restockTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Increased font size
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

        // Load initial data and update UI - InventoryMgt constructor now handles initial load
        // inventoryManager.importData(InventoryMgt.DATA_FILE); // Removed
        updateCategoryTree();
        updateItemTable();

        // For the whole window - remove previous color settings
        // Color mainBg = new Color(100, 100, 100);
        // Color cardBg = new Color(100, 100, 100);

        // getContentPane().setBackground(mainBg);
        // categoryPanel.setBackground(mainBg);
        // itemPanel.setBackground(mainBg);
        // restockTextArea.setBackground(cardBg);
        // splitPane.setBackground(new Color(49, 52, 99));
        // splitPane.setUI(...);

        setVisible(true);

        // Add keyboard shortcut for F5 (refresh)
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refreshView");
        getRootPane().getActionMap().put("refreshView", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fix for F5 refresh: Reload data and update UI components
                inventoryManager = new InventoryMgt(); // Reinitialize inventory manager to reload all data
                updateCategoryTree();
                updateItemTable();
                updateRestockAlerts();
                updateTotalWorthLabel();
                statusTextArea.setText("Inventory refreshed successfully.");
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu - Renamed to Settings Menu based on previous discussions
        JMenu fileMenu = new JMenu("File"); // Keep for now if other sub-items are needed here
        fileMenu.setText("Settings"); // Rename display text to Settings
        menuBar.add(fileMenu);

        // This part is the original Exit menu item. We'll remove it.
        // JMenuItem exitMenuItem = new JMenuItem("Exit");
        // exitMenuItem.addActionListener(e -> System.exit(0));
        // fileMenu.add(exitMenuItem);

        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        menuBar.add(settingsMenu);

        JMenuItem setLowStockThresholdMenuItem = new JMenuItem("Set Low Stock Threshold");
        setLowStockThresholdMenuItem.addActionListener(e -> showSetLowStockThresholdDialog());
        settingsMenu.add(setLowStockThresholdMenuItem);

        JMenuItem setDataFilePathMenuItem = new JMenuItem("Set Data Folder Path");
        setDataFilePathMenuItem.addActionListener(e -> showSetDataFilePathDialog());
        settingsMenu.add(setDataFilePathMenuItem);

        JMenuItem showDataFolderPathMenuItem = new JMenuItem("Show Data Folder Path");
        showDataFolderPathMenuItem.addActionListener(e -> showDataFolderPathSummary());
        settingsMenu.add(showDataFolderPathMenuItem);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        JMenuItem addItemMenuItem = new JMenuItem("Add Item");
        addItemMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        addItemMenuItem.addActionListener(e -> showAddItemDialog());
        editMenu.add(addItemMenuItem);

        JMenuItem removeItemMenuItem = new JMenuItem("Remove Item");
        removeItemMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)); // No modifier
        removeItemMenuItem.addActionListener(e -> removeSelectedItem());
        editMenu.add(removeItemMenuItem);

        JMenuItem editItemMenuItem = new JMenuItem("Edit Item");
        editItemMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)); // F2 key
        editItemMenuItem.addActionListener(e -> editSelectedItem());
        editMenu.add(editItemMenuItem);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);

        JMenuItem viewLogMenuItem = new JMenuItem("View Transaction Log");
        viewLogMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        viewLogMenuItem.addActionListener(e -> showTransactionLogDialog());
        viewMenu.add(viewLogMenuItem);

        JMenuItem refreshViewMenuItem = new JMenuItem("Refresh View");
        refreshViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)); // F5 key
        refreshViewMenuItem.addActionListener(e -> {
            inventoryManager.loadData(); // Reload all data from file
            updateItemTable();
            updateCategoryTree();
            updateRestockAlerts();
            updateTotalWorthLabel();
        });
        viewMenu.add(refreshViewMenuItem);

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
                inventoryManager.loadData(); // Reload all data from file
                updateItemTable();
                updateCategoryTree();
                updateRestockAlerts();
                updateTotalWorthLabel();
                break;
            case "Set Low Stock Threshold":
                showSetLowStockThresholdDialog();
                break;
            case "Set Data Folder Path":
                showSetDataFilePathDialog();
                break;
            case "View Folder Path Summary":
                showDataFolderPathSummary();
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

        String modelNumber = (String) itemTableModel.getValueAt(selectedRow, 0);
        String itemName = (String) itemTableModel.getValueAt(selectedRow, 1);
        String itemCategory = (String) itemTableModel.getValueAt(selectedRow, 4); // Get category for logging
        int itemQuantity = (int) itemTableModel.getValueAt(selectedRow, 3); // Get quantity for logging

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " + itemName + " (Model: " + modelNumber + ")?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                inventoryManager.removeItemByNumber(modelNumber);
                statusTextArea.append("Item removed successfully: " + itemName + "\n");
                inventoryManager.logTransaction("REMOVE", itemName, modelNumber, itemQuantity, itemCategory, 0); // Corrected arguments
                updateItemTable();
                updateCategoryTree(); // Update category tree as category might become empty
                updateRestockAlerts();
                updateTotalWorthLabel();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, "Error removing item: " + ex.getMessage(), "Removal Error", JOptionPane.ERROR_MESSAGE);
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

        String modelNumber = (String) itemTableModel.getValueAt(selectedRow, 0);
        Item editedItem = inventoryManager.searchItem(modelNumber);

        if (editedItem == null) {
            JOptionPane.showMessageDialog(this, "Item not found in inventory.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        // Get current values
        String currentName = editedItem.getModelName();
        double currentPrice = editedItem.getModelPrice();
        int currentQuantity = editedItem.getItemQuantity();
        String currentCategory = editedItem.getItemCategory();

        JTextField modelNameField = new JTextField(currentName);
        JTextField modelPriceField = new JTextField(String.valueOf(currentPrice));
        JTextField itemQuantityField = new JTextField(String.valueOf(currentQuantity));
        JTextField itemCategoryField = new JTextField(currentCategory);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Model Name:"));
        panel.add(modelNameField);
        panel.add(new JLabel("Model Price:"));
        panel.add(modelPriceField);
        panel.add(new JLabel("Item Quantity:"));
        panel.add(itemQuantityField);
        panel.add(new JLabel("Item Category:"));
        panel.add(itemCategoryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = modelNameField.getText();
                double newPrice = Double.parseDouble(modelPriceField.getText());
                int newQuantity = Integer.parseInt(itemQuantityField.getText());
                String newCategory = itemCategoryField.getText();

                // Input validation (basic)
                if (newName.trim().isEmpty() || newCategory.trim().isEmpty() || newPrice < 0 || newQuantity < 0) {
                    JOptionPane.showMessageDialog(this, "Please enter valid data for all fields (price and quantity must be non-negative).", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update the item properties
                editedItem.setModelName(newName);
                editedItem.setModelPrice(newPrice);
                editedItem.setItemCategory(newCategory);

                // Handle quantity change and update category quantity
                int oldQuantity = editedItem.getItemQuantity(); // Store old quantity before updating
                editedItem.setItemQuantity(newQuantity);
                inventoryManager.updateCategoryQuantity(currentCategory, newQuantity - oldQuantity); // Update the old category

                // If category changed, update quantities for both old and new categories
                if (!currentCategory.equalsIgnoreCase(newCategory)) {
                    // Decrease quantity in old category by new quantity, effectively moving it
                    inventoryManager.updateCategoryQuantity(currentCategory, -newQuantity);
                    // Increase quantity in new category by new quantity
                    inventoryManager.updateCategoryQuantity(newCategory, newQuantity);
                }

                statusTextArea.append("Item updated successfully: " + editedItem.getModelName() + "\n");
                inventoryManager.logTransaction("EDIT", editedItem.getModelName(), editedItem.getModelNumber(), newQuantity, editedItem.getItemCategory(), oldQuantity); // Corrected arguments

                // Save changes immediately after editing
                inventoryManager.saveData();

                updateItemTable();
                updateCategoryTree();
                updateRestockAlerts();
                updateTotalWorthLabel();
                } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price or quantity format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

        addItemDialog.getContentPane().setBackground(new Color(60, 63, 65)); // Set dialog background
        modelNumberField.setBackground(new Color(40, 40, 40));
        modelNumberField.setForeground(new Color(240, 240, 240));
        modelNumberField.setCaretColor(new Color(240, 240, 240));
        modelNameField.setBackground(new Color(40, 40, 40));
        modelNameField.setForeground(new Color(240, 240, 240));
        modelNameField.setCaretColor(new Color(240, 240, 240));
        modelPriceField.setBackground(new Color(40, 40, 40));
        modelPriceField.setForeground(new Color(240, 240, 240));
        modelPriceField.setCaretColor(new Color(240, 240, 240));
        itemQuantityField.setBackground(new Color(40, 40, 40));
        itemQuantityField.setForeground(new Color(240, 240, 240));
        itemQuantityField.setCaretColor(new Color(240, 240, 240));
        itemCategoryField.setBackground(new Color(40, 40, 40));
        itemCategoryField.setForeground(new Color(240, 240, 240));
        itemCategoryField.setCaretColor(new Color(240, 240, 240));

        // Set label colors in the dialog
        for (Component comp : addItemDialog.getContentPane().getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(new Color(240, 240, 240));
            }
        }

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.setBackground(new Color(75, 110, 175)); // Example button color
        saveButton.setForeground(new Color(240, 240, 240));
        cancelButton.setBackground(new Color(175, 75, 75)); // Example button color
        cancelButton.setForeground(new Color(240, 240, 240));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBackground(new Color(60, 63, 65)); // Set button panel background

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
                    statusTextArea.append("Item added successfully: " + newItem.getModelName() + "\n");
                    inventoryManager.logTransaction("ADD", newItem.getModelName(), newItem.getModelNumber(), newItem.getItemQuantity(), newItem.getItemCategory(), 0); // Corrected arguments

                    updateItemTable();
                    updateCategoryTree();
                    updateRestockAlerts();
                    updateTotalWorthLabel();
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
        logDialog.setSize(700, 500);
        logDialog.setLayout(new BorderLayout());
        logDialog.setLocationRelativeTo(this);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(UIManager.getColor("TextPane.background"));
        logArea.setForeground(UIManager.getColor("TextPane.foreground"));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Load logs from InventoryMgt (which now reads from Availability)
        List<String> logs = inventoryManager.getTransactionLogs(); // Corrected method call
        for (String log : logs) {
            logArea.append(log + "\n");
        }

        logDialog.add(new JScrollPane(logArea), BorderLayout.CENTER);
        logDialog.setVisible(true);
    }

    // Method to show dialog for setting low stock threshold
    private void showSetLowStockThresholdDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter new low stock threshold:", "Set Low Stock Threshold", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newThreshold = Integer.parseInt(input.trim());
                if (newThreshold < 0) {
                    JOptionPane.showMessageDialog(this, "Threshold cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                inventoryManager.setLowStockThreshold(newThreshold); // This now saves and logs
                updateRestockAlerts(); // Refresh alerts with new threshold
                JOptionPane.showMessageDialog(this, "Low stock threshold set to " + newThreshold + ".", "Threshold Updated", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for threshold.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to show dialog for setting data file path
    private void showSetDataFilePathDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Inventory Data Folder");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            if (folder != null) {
                // Construct the potential inventory_data.csv path in the chosen folder
                File potentialDataFile = new File(folder.getAbsolutePath() + File.separator + "inventory_data.csv");

                int confirmLoad = JOptionPane.NO_OPTION; // Default to not loading
                if (potentialDataFile.exists()) {
                    confirmLoad = JOptionPane.showConfirmDialog(this,
                            "An 'inventory_data.csv' file already exists in this folder.\nDo you want to load the existing data from it? (Choosing 'No' will start with a blank inventory for this path.)",
                            "Load Existing Data?",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                }

                try {
                    // First, set the new data file path in the manager (this updates Availability's path)
                    inventoryManager.setDataFilePath(folder.getAbsolutePath());

                    if (confirmLoad == JOptionPane.YES_OPTION && potentialDataFile.exists()) {
                        // If user confirmed to load and file exists, force a reload from the new path
                        inventoryManager.loadData();
                        JOptionPane.showMessageDialog(this, "Data loaded from:\n" + potentialDataFile.getAbsolutePath(), "Path Updated & Data Loaded", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // If no file exists, or user chose not to load, start with a blank inventory
                        // This is achieved by reinitializing the manager and then updating UI components,
                        // which ensures a fresh start without an existing file's data.
                        inventoryManager = new InventoryMgt(); // Reinitialize to load blank data
                        inventoryManager.setDataFilePath(folder.getAbsolutePath()); // Set the new path for future saves
                        JOptionPane.showMessageDialog(this, "Data folder path set to:\n" + folder.getAbsolutePath() + "\n(New inventory_data.csv will be created on first save.)", "Path Updated", JOptionPane.INFORMATION_MESSAGE);
                    }

                    // Refresh GUI after path change/load
                    updateItemTable();
                    updateCategoryTree();
                    updateRestockAlerts();
                    updateTotalWorthLabel();
                    showDataFolderPathSummary(); // Update the displayed path immediately
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error setting data path: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    // Catch any other unexpected errors during data loading/setting
                    JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Print stack trace for debugging
                }
            }
        }
    }

    // New method to show current data folder path summary
    private void showDataFolderPathSummary() {
        // Retrieve the current data file path from InventoryMgt
        String currentPath = inventoryManager.getDataFilePath();
        JOptionPane.showMessageDialog(this, "Current Inventory Data Folder:\n" + currentPath, "Data Folder Path Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to update the item table
    public void updateItemTable() {
        // Clear existing data
        itemTableModel.setRowCount(0);
        // Get data from inventoryManager and add to table model
        List<Item> items = inventoryManager.getInventoryItems();
        // Category-based restock alerts
        Map<String, Integer> restockCount = new HashMap<>();
        double totalWorth = 0.0; // Variable to store cumulative worth

        for (Item item : items) {
            itemTableModel.addRow(new Object[]{item.getModelNumber(), item.getModelName(), item.getModelPrice(), item.getItemQuantity(), item.getItemCategory()});
            totalWorth += item.getModelPrice() * item.getItemQuantity(); // Calculate cumulative worth

            int lowStockThreshold = inventoryManager.getLowStockThreshold(); // Use the threshold from InventoryMgt
            if (item.getItemQuantity() < lowStockThreshold) {
                String category = item.getItemCategory();
                restockCount.put(category, restockCount.getOrDefault(category, 0) + 1);
            }
        }
        // Update cumulative worth display (you'll need to add a JLabel for this)
        // For now, let's put it in the status bar for demonstration
        totalWorthLabel.setText("Total Inventory Worth: Php" + String.format("%.2f", totalWorth));

        StringBuilder restockAlerts = new StringBuilder();
        for (Map.Entry<String, Integer> entry : restockCount.entrySet()) {
            restockAlerts.append("Restock needed in category: ")
                         .append(entry.getKey())
                         .append(" (" + entry.getValue() + " item(s) left)\n");
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
        categoryTreeModel = new DefaultTreeModel(root);

        // Get categories from InventoryMgt and sort them
        List<Category> categories = inventoryManager.getItemCategories();
        categories.sort((c1, c2) -> c1.getCategoryName().compareToIgnoreCase(c2.getCategoryName()));

        for (Category category : categories) {
            root.add(new DefaultMutableTreeNode(category.getCategoryName()));
        }

         categoryTree.setModel(categoryTreeModel);
        for (int i = 0; i < categoryTree.getRowCount(); i++) {
            categoryTree.expandRow(i);
        }
        // Select the root node after updating the tree
        categoryTree.setSelectionRow(0);
    }

    public void updateRestockAlerts() {
        StringBuilder alerts = new StringBuilder();
        alerts.append("Restock Alerts:\n");
        boolean needsUpdate = false;

        List<Category> categories = inventoryManager.getItemCategories();
        categories.sort((c1, c2) -> c1.getCategoryName().compareToIgnoreCase(c2.getCategoryName())); // Sort for consistent display

        for (Category category : categories) {
            if (category.getCategoryQuantity() <= inventoryManager.getLowStockThreshold()) { // Use configurable threshold
                alerts.append(String.format("Restock needed in category: %s (%d item(s) low)\n", category.getCategoryName(), category.getCategoryQuantity()));
                needsUpdate = true;
            }
        }

        if (!needsUpdate) {
            alerts.append("No restock alerts at this time.\n");
        }
        restockTextArea.setText(alerts.toString());
    }

    public void updateTotalWorthLabel() {
        double totalWorth = 0.0;
        List<Item> items = inventoryManager.getInventoryItems();
        for (Item item : items) {
            totalWorth += item.getModelPrice() * item.getItemQuantity();
        }
        totalWorthLabel.setText("Total Inventory Worth: Php" + String.format("%.2f", totalWorth));
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