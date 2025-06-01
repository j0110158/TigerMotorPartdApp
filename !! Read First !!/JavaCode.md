# Java Code Overview

This document provides an overview of the key Java files in the Tiger Motor Parts Inventory Management System and their primary functions and methods.

## `Main.java`

This is the application's entry point. Its main responsibility is to initialize and launch the user interface.

- `main(String[] args)`: The standard main method. It uses `SwingUtilities.invokeLater` to ensure the Swing GUI is created and run on the Event Dispatch Thread (EDT), which is the standard practice for Swing applications.

## `InventorySwingGUI.java`

This file contains the implementation of the Swing-based Graphical User Interface (GUI). It handles all user interactions and displays inventory data, interacting with the `InventoryMgt` class for data operations.

- `InventorySwingGUI()`: The constructor sets up the main JFrame window, including its title, default close operation, size, layout (`BorderLayout`), and the system look and feel. It initializes UI components like the menu bar, split pane, category tree (`JTree`), item table (`JTable`), search field (`JTextField`), and status label (`JLabel`). It also sets up listeners for tree selections and button/menu actions and loads initial data.
- `createMenuBar()`: A helper method to construct the application's menu bar, adding File, Edit, and View menus with various JMenuItems (Import, Export, Exit, Add Item, Remove Item, Edit Item, Refresh, Transaction Log). Accelerators (keyboard shortcuts) and tooltips are configured here.
- `actionPerformed(ActionEvent e)`: Implements the `ActionListener` interface. This central method receives action events from menu items and buttons and delegates handling based on the action command string (e.g., "Add Item", "Search", "Exit").
- `filterItems()`: Filters the items displayed in the `itemTable` based on the text entered in the `searchField`. It searches across model number, model name, and category.
- `filterItemsByCategory(String categoryName)`: Filters the items displayed in the `itemTable` to show only items belonging to the specified category. If "All Categories" is selected, it shows all items.
- `removeSelectedItem()`: Handles the removal of the item currently selected in the `itemTable`. It prompts the user for confirmation, calls the `inventoryManager` to remove the item, logs the transaction, saves data, and updates the UI.
- `editSelectedItem()`: Opens a modal `JDialog` allowing the user to modify the details of the item currently selected in the `itemTable`. It retrieves current item data, populates text fields, and on saving, updates the item via `inventoryManager`, saves data, and refreshes the UI.
- `showAddItemDialog()`: Opens a modal `JDialog` for adding a new item. It collects input for model number, name, price, and category. It includes basic validation and prompts to create a new category if the entered one doesn't exist. Upon saving, it adds the item via `inventoryManager`, logs the transaction, saves data, and updates the UI.
- `showTransactionLogDialog()`: Opens a modal `JDialog` to display the transaction history read from the log file (`transaction_log.csv`) in a `JTable`.
- `importData()`: Opens a `JFileChooser` to allow the user to select a CSV file for importing inventory data using `inventoryManager.importData()`. Updates the UI and status label upon completion or error.
- `exportData()`: Opens a `JFileChooser` for the user to specify a location and filename to save the current inventory data as a CSV file using `inventoryManager.exportData()`. Updates the status label upon completion or error.
- `updateItemTable()`: Refreshes the `itemTable` by clearing its current data and repopulating it with the latest inventory items from `inventoryManager.getInventoryItems()`.
- `updateCategoryTree()`: Refreshes the `categoryTree` by clearing the existing tree model and rebuilding it based on the current categories obtained from `inventoryManager.getItemCategories()`.

## `InventoryMgt.java`

This class contains the core business logic for managing the inventory, categories, and transactions. It handles data loading, saving, adding, removing, searching, and category management.

- `InventoryMgt()`: Constructor initializes the internal data structures (`inventoryItems`, `itemCategories`, `transactionLogs`) and sets up file paths.
- `importData(String filePath)`: Reads inventory, category, and transaction log data from a specified CSV file. It parses the data based on type prefixes (CATEGORY, ITEM, LOG) and populates the respective internal lists. Includes error handling for file not found.
- `exportData(String filePath)`: Writes the current inventory, category, and transaction log data to a specified CSV file in a structured format, using type prefixes.
- `saveData()`: Saves the current state of inventory, categories, and transaction logs to the default data files (`inventory_data.csv` and `transaction_log.csv`).
- `addItem(Item item)`: Adds a new `Item` to the `inventoryItems` list and updates the quantity of its corresponding category.
- `removeItemByNumber(String modelNumber)`: Removes an item from the `inventoryItems` list based on its model number and updates the quantity of its category.
- `removeItemByCategory(String categoryName)`: Removes all items belonging to a specific category.
- `searchItem(String searchTerm)`: Searches for an item by model number or model name.
- `addCategory(String categoryName, int initialQuantity)`: Adds a new `Category` if it doesn't already exist.
- `removeCategory(String categoryName)`: Removes a category and all associated items.
- `findCategoryByName(String categoryName)`: Finds and returns a `Category` object by its name.
- `viewCategories()`: (Used in Console version, not directly in Swing GUI) Prints the list of categories to the console.
- `getInventoryItems()`: Returns the list of all `Item` objects.
- `getItemCategories()`: Returns the list of all `Category` objects.
- `logTransaction(String action, String modelName, String modelNumber, int quantity)`: Records a transaction event (ADD, REMOVE, EDIT) with a timestamp and relevant item details to the `transactionLogs` list.
- `readTransactionLogsFromFile()`: Reads and returns the list of transaction log entries from the `transaction_log.csv` file.
- `dataFileExists()`: Checks if the default data file exists.
- `createDataFile()`: Creates an empty default data file if it doesn't exist.

## `Item.java`

A simple class representing an inventory item.

- Constructor: Initializes an `Item` with model details, quantity, and category.
- Getters and Setters: Provide access to the item's properties (`modelPrice`, `modelName`, `modelNumber`, `itemQuantity`, `itemCategory`). Note that `itemQuantity` is effectively fixed at 1 in the current application logic.

## `Category.java`

A simple class representing an item category.

- Constructor: Initializes a `Category` with a name and quantity.
- Getters and Setters: Provide access to the category's properties (`categoryName`, `categoryQuantity`).
- `addItemQuantity(int quantity)`: Increases the category's quantity.
- `removeItemQuantity(int quantity)`: Decreases the category's quantity.

## `Availability.java`

This class (primarily used in the Console version) provides basic availability checking and restock warnings based on item and category quantities.

- `checkAvailability(int quantity)`: Returns true if the quantity is greater than 0.
- `displayRestockWarning(String itemName, int itemQuantity, int categoryQuantity, String categoryName)`: (Used in Console version) Displays a restock warning based on category quantity.

## `Console.java` (Commented Out)

This file contained the original command-line interface for the application. It is now commented out and not used by the `Main` class when launching the Swing GUI.
