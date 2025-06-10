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
            System.err.println("Error setting Look and Feel: " + e.getMessage());
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
        try {
        String command = e.getActionCommand();
            System.out.println("Action Command: " + command);

        switch (command) {
            case "Add Item":
                showAddItemDialog();
                break;
            case "Remove Item":
                removeSelectedItem();
                break;
            case "Edit Item":
                editSelectedItem();
                break;
                case "Search":
                    filterItems();
                    break;
                case "View Transaction Log":
                    showTransactionLogDialog();
                break;
            case "Set Low Stock Threshold":
                showSetLowStockThresholdDialog();
                break;
            case "Set Data Folder Path":
                showSetDataFilePathDialog();
                break;
                case "Refresh":
                    updateItemTable();
                    updateCategoryTree();
                    updateRestockAlerts();
                    updateTotalWorthLabel();
                    JOptionPane.showMessageDialog(this, "Inventory refreshed!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
                break;
                default:
                    System.out.println("Unhandled action command: " + command);
                break;
            }
        } catch (Exception ex) {
            System.err.println("Error in actionPerformed: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to filter items in the table based on search field text
    private void filterItems() {
        try {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                updateItemTable();
                return;
            }

            Item foundItem = inventoryManager.searchItem(searchTerm);
            if (foundItem != null) {
                itemTableModel.setRowCount(0); // Clear existing data
                itemTableModel.addRow(new Object[]{foundItem.getModelNumber(), foundItem.getModelName(), foundItem.getModelPrice(), foundItem.getItemQuantity(), foundItem.getItemCategory()});
            } else {
                // If not found by item search, try filtering by category
                boolean foundCategory = false;
                for (Category category : inventoryManager.getItemCategories()) {
                    if (category.getCategoryName().equalsIgnoreCase(searchTerm)) {
                        filterItemsByCategory(category.getCategoryName());
                        foundCategory = true;
                        break;
                    }
                }
                if (!foundCategory) {
                    JOptionPane.showMessageDialog(this, "Item or category not found: " + searchTerm, "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    updateItemTable(); // Show all items if nothing is found
                }
            }
        } catch (Exception ex) {
            System.err.println("Error filtering items: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred during search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to filter items in the table by category
    private void filterItemsByCategory(String categoryName) {
        try {
            itemTableModel.setRowCount(0); // Clear existing data
            for (Item item : inventoryManager.getInventoryItems()) {
                if (item.getItemCategory().equalsIgnoreCase(categoryName)) {
                itemTableModel.addRow(new Object[]{item.getModelNumber(), item.getModelName(), item.getModelPrice(), item.getItemQuantity(), item.getItemCategory()});
            }
        }
        } catch (Exception ex) {
            System.err.println("Error filtering items by category: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while filtering by category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to remove the selected item from the table and backend
    private void removeSelectedItem() {
        try {
        int selectedRow = itemTable.getSelectedRow();
            if (selectedRow >= 0) {
        String modelNumber = (String) itemTableModel.getValueAt(selectedRow, 0);
                String itemName = (String) itemTableModel.getValueAt(selectedRow, 1);
                int quantity = (int) itemTableModel.getValueAt(selectedRow, 3);

                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " + itemName + " (Model: " + modelNumber + ")?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
                inventoryManager.removeItemByNumber(modelNumber);
                    JOptionPane.showMessageDialog(this, itemName + " removed successfully.", "Item Removed", JOptionPane.INFORMATION_MESSAGE);
                    updateAllGUIComponents(); // Refresh all GUI components after removal
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            }
            } catch (Exception ex) {
            System.err.println("Error removing selected item: " + ex.getMessage());
                ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while removing item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to edit the selected item in the table and backend
    private void editSelectedItem() {
        try {
        int selectedRow = itemTable.getSelectedRow();
            if (selectedRow >= 0) {
        String currentModelNumber = (String) itemTableModel.getValueAt(selectedRow, 0);
                Item itemToEdit = inventoryManager.findItemByModelNumber(currentModelNumber);

                if (itemToEdit != null) {
                    JTextField modelNumberField = new JTextField(itemToEdit.getModelNumber());
                    JTextField modelNameField = new JTextField(itemToEdit.getModelName());
                    JTextField modelPriceField = new JTextField(String.valueOf(itemToEdit.getModelPrice()));
                    JTextField itemQuantityField = new JTextField(String.valueOf(itemToEdit.getItemQuantity()));
                    JTextField itemCategoryField = new JTextField(itemToEdit.getItemCategory());

                    modelNumberField.setEditable(false); // Model number should not be editable

                    JPanel panel = new JPanel(new GridLayout(0, 2));
                    panel.add(new JLabel("Model Number:"));
                    panel.add(modelNumberField);
                    panel.add(new JLabel("Model Name:"));
                    panel.add(modelNameField);
                    panel.add(new JLabel("Price:"));
                    panel.add(modelPriceField);
                    panel.add(new JLabel("Quantity:"));
                    panel.add(itemQuantityField);
                    panel.add(new JLabel("Category:"));
                    panel.add(itemCategoryField);

                    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        String newModelName = modelNameField.getText().trim();
                        double newModelPrice = Double.parseDouble(modelPriceField.getText().trim());
                        int newItemQuantity = Integer.parseInt(itemQuantityField.getText().trim());
                        String newItemCategory = itemCategoryField.getText().trim();

                        // Remove the old item from inventory and its category
                        inventoryManager.removeItemByNumber(currentModelNumber);

                        // Create and add the updated item
                        Item updatedItem = new Item(newModelPrice, newModelName, currentModelNumber, newItemQuantity, newItemCategory);
                        inventoryManager.addItem(updatedItem);

                        JOptionPane.showMessageDialog(this, "Item updated successfully.", "Item Updated", JOptionPane.INFORMATION_MESSAGE);
                        updateAllGUIComponents(); // Refresh all GUI components after edit
                    }
                        } else {
                    JOptionPane.showMessageDialog(this, "Selected item not found in inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            }
                } catch (NumberFormatException ex) {
            System.err.println("Error parsing number during item edit: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid number format for price or quantity. Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
            System.err.println("Error editing selected item: " + ex.getMessage());
                    ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while editing item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to show the Add Item dialog
    private void showAddItemDialog() {
        try {
            JTextField modelNumberField = new JTextField();
            JTextField modelNameField = new JTextField();
            JTextField modelPriceField = new JTextField();
            JTextField itemQuantityField = new JTextField();
            JTextField itemCategoryField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Model Number:"));
            panel.add(modelNumberField);
            panel.add(new JLabel("Model Name:"));
            panel.add(modelNameField);
            panel.add(new JLabel("Price:"));
            panel.add(modelPriceField);
            panel.add(new JLabel("Quantity:"));
            panel.add(itemQuantityField);
            panel.add(new JLabel("Category:"));
            panel.add(itemCategoryField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String modelNumber = modelNumberField.getText().trim();
                String modelName = modelNameField.getText().trim();
                double modelPrice = Double.parseDouble(modelPriceField.getText().trim());
                int itemQuantity = Integer.parseInt(itemQuantityField.getText().trim());
                String itemCategory = itemCategoryField.getText().trim();

                    Item newItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                    inventoryManager.addItem(newItem);

                JOptionPane.showMessageDialog(this, "Item added successfully!", "Add Item", JOptionPane.INFORMATION_MESSAGE);
                updateAllGUIComponents(); // Refresh all GUI components after adding
            }
                } catch (NumberFormatException ex) {
            System.err.println("Error parsing number during add item: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid number format for price or quantity. Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            System.err.println("Error adding item (Invalid argument): " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            System.err.println("Error adding item (Illegal state): " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
            System.err.println("Unknown error adding item: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred while adding item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to show the transaction log dialog
    private void showTransactionLogDialog() {
        try {
        JDialog logDialog = new JDialog(this, "Transaction Log", true);
            logDialog.setSize(600, 400);
        logDialog.setLayout(new BorderLayout());

            JTextArea logTextArea = new JTextArea();
            logTextArea.setEditable(false);
            logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            List<String> logs = inventoryManager.getTransactionLogs();
            for (String log : logs) {
                logTextArea.append(log + "\n");
            }

            logDialog.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> logDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        logDialog.add(buttonPanel, BorderLayout.SOUTH);

            logDialog.setLocationRelativeTo(this);
        logDialog.setVisible(true);
        } catch (Exception ex) {
            System.err.println("Error showing transaction log dialog: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while displaying transaction log: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to show dialog for setting low stock threshold
    private void showSetLowStockThresholdDialog() {
            try {
            String currentThreshold = String.valueOf(inventoryManager.getLowStockThreshold());
            String input = JOptionPane.showInputDialog(this, "Enter new low stock threshold:", "Set Low Stock Threshold", JOptionPane.PLAIN_MESSAGE, null, null, currentThreshold).toString();
            if (input != null && !input.trim().isEmpty()) {
                int newThreshold = Integer.parseInt(input.trim());
                if (newThreshold < 0) {
                    JOptionPane.showMessageDialog(this, "Threshold cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                inventoryManager.setLowStockThreshold(newThreshold);
                updateRestockAlerts(); // Update alerts after setting new threshold
                JOptionPane.showMessageDialog(this, "Low stock threshold set to: " + newThreshold, "Threshold Set", JOptionPane.INFORMATION_MESSAGE);
            }
            } catch (NumberFormatException ex) {
            System.err.println("Error parsing number during low stock threshold setting: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number for the threshold.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.err.println("Error setting low stock threshold: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while setting low stock threshold: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to show dialog for setting data file path
    private void showSetDataFilePathDialog() {
        try {
            JFileChooser fileChooser = new JFileChooser(inventoryManager.getDataFilePath()); // Start in current data directory
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select Folder to Save Data");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                inventoryManager.setDataFilePath(selectedFolder.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Data will now be saved in: " + selectedFolder.getAbsolutePath(), "Data Path Set", JOptionPane.INFORMATION_MESSAGE);
                updateAllGUIComponents(); // Refresh GUI after path change
            }
            } catch (Exception ex) {
            System.err.println("Error setting data file path: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while setting data file path: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        try {
            itemTableModel.setRowCount(0); // Clear existing data
            for (Item item : inventoryManager.getInventoryItems()) {
            itemTableModel.addRow(new Object[]{item.getModelNumber(), item.getModelName(), item.getModelPrice(), item.getItemQuantity(), item.getItemCategory()});
            }
        } catch (Exception ex) {
            System.err.println("Error updating item table: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the item table: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to update the category tree
    public void updateCategoryTree() {
        try {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Categories");
            categoryTreeModel = new DefaultTreeModel(root);
            categoryTree.setModel(categoryTreeModel);

            // Use a Set to keep track of categories already added to the tree
            Set<String> addedCategories = new HashSet<>();

            // Add categories from inventory items first to ensure all existing categories are shown
            for (Item item : inventoryManager.getInventoryItems()) {
                String categoryName = item.getItemCategory();
                if (!addedCategories.contains(categoryName)) {
                    root.add(new DefaultMutableTreeNode(categoryName));
                    addedCategories.add(categoryName);
                }
            }

            // Add categories from itemCategories list (might contain empty categories)
            for (Category category : inventoryManager.getItemCategories()) {
                String categoryName = category.getCategoryName();
                if (!addedCategories.contains(categoryName)) {
                    root.add(new DefaultMutableTreeNode(categoryName));
                    addedCategories.add(categoryName);
                }
            }

            categoryTreeModel.reload();
            // Expand all nodes for better visibility
            for (int i = 0; i < categoryTree.getRowCount(); i++) {
                categoryTree.expandRow(i);
            }
        } catch (Exception ex) {
            System.err.println("Error updating category tree: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the category tree: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateRestockAlerts() {
        try {
            restockTextArea.setText(""); // Clear previous alerts
            List<Category> lowStockCategories = inventoryManager.getLowStockCategories();
            if (!lowStockCategories.isEmpty()) {
                restockTextArea.append("--- LOW STOCK ALERTS (Categories) ---\n");
                for (Category category : lowStockCategories) {
                    restockTextArea.append("Category: " + category.getCategoryName() + " - Total Quantity: " + category.getCategoryQuantity() + "\n");
                }
                restockTextArea.append("-------------------------------------\n");
        } else {
                restockTextArea.append("No low stock categories.");
            }
        } catch (Exception ex) {
            System.err.println("Error updating restock alerts: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating restock alerts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTotalWorthLabel() {
        try {
            double totalWorth = inventoryManager.calculateTotalInventoryWorth();
            totalWorthLabel.setText(String.format("Total Inventory Worth: Php%,.2f", totalWorth));
        } catch (Exception ex) {
            System.err.println("Error updating total worth label: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while calculating total worth: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAllGUIComponents() {
        try {
            inventoryManager.loadData(); // Reload data to ensure everything is fresh
            updateItemTable();
            updateCategoryTree();
            updateRestockAlerts();
            updateTotalWorthLabel();
        } catch (Exception ex) {
            System.err.println("Error updating all GUI components: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while refreshing the GUI: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set a global uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception in thread " + t.getName() + ":");
            e.printStackTrace();
            // Optionally, show a dialog to the user
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage() + "\nCheck console for details.", "Fatal Error", JOptionPane.ERROR_MESSAGE);
        });

        SwingUtilities.invokeLater(() -> {
            try {
                new InventorySwingGUI().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error launching InventorySwingGUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
} 