# Tiger Motorhub App - Java Code Structure

This document outlines the core Java files that constitute the Tiger Motorhub Inventory Management application, detailing their responsibilities and how they interact.

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

### `main.java`
*   **Purpose**: (Presumed) Often contains the `main` method which serves as the entry point for the entire application, initiating the `InventorySwingGUI` or other components.

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
