# Tiger Motorhub App - Codebase Details

This document provides an overview of the core Java code structure and how data is managed and persisted within the Tiger Motorhub Inventory Management application.

## Core Application Modules

### `InventorySwingGUI.java`
*   **Purpose**: This is the main graphical user interface (GUI) class of the application. It extends `JFrame` and sets up the entire visual layout, including menus, tables, trees, search bars, and status areas.
*   **Key Responsibilities**:
    *   Initializes and displays all Swing components.
    *   Handles user interactions (button clicks, menu selections, table actions, tree selections).
    *   Coordinates with `InventoryMgt` to display and update inventory data.
    *   Manages dialogs for adding, editing, and setting inventory parameters.
    *   Displays cumulative inventory worth and restock alerts.
*   **Recent Changes**:
    *   "File" menu renamed to "Settings".
    *   Added menu options for "Set Low Stock Threshold" and "Set Data File Path".
    *   Removed explicit "Import Data" and "Export Data" menu items.
    *   Integrated display for total inventory worth.
    *   Updated to communicate with `InventoryMgt` for managing low stock threshold and data file paths.

### `InventoryMgt.java`
*   **Purpose**: This class acts as the central business logic and data manager for the inventory. It maintains lists of `Item` and `Category` objects and provides methods for inventory operations.
*   **Key Responsibilities**:
    *   Manages the collection of `Item` and `Category` objects.
    *   Provides methods for adding, removing, searching, and updating inventory items and categories.
    *   Manages transaction logging.
    *   Holds the application-wide `lowStockThreshold` and `dataFilePath`.
    *   Delegates data persistence operations (reading/writing to file) to the `Availability` class.
    *   Loads and saves data through the `Availability` instance.
*   **Recent Changes**:
    *   Fixed the "Error removing item: Amount cannot be negative" by correctly using `category.decreaseQuantity()`.
    *   Introduced configurable `lowStockThreshold` with getter/setter.
    *   Introduced configurable `dataFilePath` with getter/setter.
    *   Refactored data persistence methods (`importData`, `exportData`, `saveData`, `readTransactionLogsFromFile`) to use the `Availability` class.
    *   Constructor now handles initial data loading using `Availability`.

### `Availability.java`
*   **Purpose**: This class is dedicated to handling data persistence (reading from and writing to files) and general availability checks. It acts as a utility class for file I/O operations related to the inventory.
*   **Key Responsibilities**:
    *   Manages the `dataFilePath` for inventory data and transaction logs.
    *   Provides methods to `readDataFromFile()` and `writeDataToFile()` for general inventory data.
    *   Provides methods to `readTransactionLogsFromFile()` and `writeTransactionLogsToFile()` specifically for transaction logs.
    *   Ensures the data file exists, creating it if necessary.
    *   Includes utility methods for checking item availability and displaying restock warnings.
*   **Recent Changes**:
    *   Refactored and moved data persistence methods from `InventoryMgt.java` into this class.
    *   Constructor now takes `dataFilePath` and ensures file creation.
    *   Added `setDataFilePath()` to update the active data file path, which also ensures the new file path's file exists.

## Data Models

### `Item.java`
*   **Purpose**: Represents a single inventory item. It is a simple data class holding attributes of a motorcycle part.
*   **Key Attributes**: Model Number, Model Name, Price, Quantity (fixed at 1 as per application logic), Category.

### `Category.java`
*   **Purpose**: Represents a category for inventory items. It manages the name and cumulative quantity of items within that category.
*   **Key Responsibilities**:
    *   Stores `categoryName` and `categoryQuantity`.
    *   Provides methods to `increaseQuantity()` and `decreaseQuantity()`.
    *   Includes logic to check if a category `needsRestock()`.
*   **Role in Bug Fix**: The `decreaseQuantity` method in this class was crucial for resolving the "Amount cannot be negative" error, as it safely handles quantity decrements without throwing an error when the quantity would otherwise go below zero.

## Other Relevant Files

### `Console.java`
*   **Purpose**: (Presumed) Likely provides a text-based or command-line interface for interacting with the inventory management system, potentially for testing or alternative use cases, separate from the Swing GUI.

### Data Files
*   `inventory_data.csv`: The primary data file for inventory items, categories, and transaction logs. Its path is now configurable via the UI.
*   `inventory.csv`: (Legacy) Likely an older or test inventory file, not actively used by the current application logic for data persistence.
*   `logbook.csv`: (Legacy) Likely an older or test transaction log file, not actively used by the current application logic for data persistence, as logs are now stored in `inventory_data.csv`.
*   `saved_items.csv`: (Legacy) Likely an older or test file for saved items, not actively used by the current application logic for data persistence.

This structure ensures a separation of concerns, making the application more maintainable and scalable. The GUI handles presentation, `InventoryMgt` manages business logic, and `Availability` handles data persistence, while `Item` and `Category` serve as fundamental data models.

### UI Theming

**Consistent Dark Theme Implementation**

The application's UI now features a consistent dark theme, implemented by configuring Java Swing's `UIManager` properties. This approach ensures that the Nimbus Look and Feel (L&F) renders all components with the desired dark palette, maintaining visual consistency across the entire application.

**Why `UIManager.put()` was used:**

Previously, attempts to apply a dark theme by directly setting `setBackground()` and `setForeground()` on individual components led to an inconsistent and visually unappealing UI. This was due to conflicts with the Nimbus L&F's internal rendering logic for borders, gradients, and other visual elements. By using `UIManager.put()`, we instruct Nimbus to use the specified colors when drawing its components, thereby ensuring that all UI elements adhere to the new theme cohesively.

**Key `UIManager` properties configured for the dark theme:**

*   `control`: General background color for controls.
*   `nimbusBase`: Primary color for interactive elements.
*   `nimbusBlueGrey`: Secondary background color.
*   `text`: Default text color for most components.
*   `nimbusLightBackground`: Overall application background.
*   `nimbusFocus`: Color for focused components.
*   `Panel.background`, `Label.foreground`, `TitledBorder.titleColor`
*   `Button.background`, `Button.foreground`, `Button.light`, `Button.highlight`
*   `TextField.background`, `TextField.foreground`, `TextField.caretForeground`
*   `TextArea.background`, `TextArea.foreground`
*   `TextPane.background`, `TextPane.foreground`
*   `Table.background`, `Table.foreground`, `Table.selectionBackground`, `Table.selectionForeground`, `Table.gridColor`
*   `TableHeader.background`, `TableHeader.foreground`
*   `Tree.background`, `Tree.foreground`, `Tree.selectionBackground`, `Tree.selectionForeground`, `Tree.textBackground`, `Tree.textForeground`, `Tree.hash`
*   `MenuBar.background`, `MenuBar.foreground`, `Menu.background`, `Menu.foreground`, `MenuItem.background`, `MenuItem.foreground`, `PopupMenu.background`
*   `ScrollPane.background`
*   `SplitPane.background`, `SplitPaneDivider.draggingColor`
*   `OptionPane.background`, `OptionPane.messageForeground`, `OptionPane.buttonAreaBackground`

This approach ensures that all UI elements, including complex components like tables and trees, seamlessly integrate with the new dark theme, avoiding visual conflicts and providing a unified user experience.

---

# Data Management in Tiger Motorhub App

This document outlines how data is managed and persisted within the Tiger Motorhub application, following recent updates.

## Data Persistence

The application automatically saves and loads inventory data and transaction logs to and from a CSV file. This ensures that your inventory state is preserved across application sessions.

-   **Automatic Saving**: All changes to the inventory (adding, removing, or editing items) and new transactions are automatically saved.
-   **Automatic Loading**: Upon application startup, the most recently configured data file is loaded to restore the previous inventory state.

## Data File Location

The primary data file is `inventory_data.csv`. By default, this file is located in the application's working directory.

### Customizing Data File Path

Users now have the flexibility to specify a custom file path for `inventory_data.csv`. This feature is accessible through the application's UI:

1.  Navigate to the **Settings** menu in the application.
2.  Select **Set Data File Path**.
3.  A file chooser dialog will appear, allowing you to browse and select a new location for your `inventory_data.csv` file. You can choose an existing file or specify a new file name and location.
4.  Once a new path is selected, the application will attempt to create the file if it doesn't exist and will then reload the inventory from this new path.

This feature is particularly useful for users who wish to:
*   Store their inventory data in a specific directory (e.g., a shared network drive, a cloud-synced folder).
*   Manage multiple inventory datasets by pointing the application to different CSV files.

## Deprecation of Explicit Import/Export

With the introduction of the configurable data file path and automatic data management, the explicit "Import Data" and "Export Data" menu options have been removed from the user interface. The philosophy is that by setting the `inventory_data.csv` path, the user effectively controls where data is sourced from and saved to, making separate import/export operations redundant for the main inventory data.

## `inventory_data.csv` Structure

The `inventory_data.csv` file is a single CSV file that stores all three types of data (Categories, Items, and Transaction Logs) in a structured format. Each line in the file represents a single record, prefixed by a type identifier to distinguish between different data entries.

The structure uses a comma (`,`) as the delimiter.

Here's the format for each record type:

1.  **Categories:**
    -   Prefix: `CATEGORY`
    -   Format: `CATEGORY,categoryName`
    -   Example: `CATEGORY,Electronics`

2.  **Items:**
    -   Prefix: `ITEM`
    -   Format: `ITEM,modelNumber,modelName,modelPrice,itemQuantity,itemCategory`
    -   Example: `ITEM,ENG-001,Engine Block,5500.00,1,Engines`
    -   *Note: `itemQuantity` is always stored as 1 as per the application's logic.* `modelPrice` is stored as a double.

3.  **Transaction Logs:**
    -   Prefix: `LOG`
    -   Format: `LOG,timestamp,action,modelName,modelNumber,quantity,category`
    -   Example: `LOG,2023-10-27 10:30:01,ADD,Brake Pad Set,BRK-P01,1,Brakes`
    -   *Note: The quantity here refers to the quantity involved in the specific transaction.* The category is included for context.

This combined format allows the `InventoryMgt` and `Availability` classes to read and write all application data to and from a single, easily parsable file. 