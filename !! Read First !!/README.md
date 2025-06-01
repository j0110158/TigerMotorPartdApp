# Tiger Motor Parts Inventory Management System

A comprehensive inventory management system for Tiger Motor Parts, now featuring a Java Swing Graphical User Interface.

## Features

- **Modern Swing GUI**
  - Resizable window with standard Windows controls.
  - Title: "TIGER MOTORHUB APP".
  - Minimum size: 1024x600.
  - Category tree for easy navigation.
  - Searchable and sortable item table.
  - Right-click context menu on table items (Edit, Remove).
  - Status bar displaying application status.

- **Inventory Management**
  - Add new items via dialog.
  - Edit existing items via dialog.
  - Remove selected items.
  - Utilizes existing `InventoryMgt` backend logic.
  - Item quantity is fixed at 1 as per current logic.

- **Category Management**
  - Categories displayed in a hierarchical tree view.
  - Automatic category creation when adding items.
  - Filtering items based on selected category in the tree.

- **Data Management**
  - Import/Export inventory data to CSV files.
  - View transaction logs in a dedicated dialog.
  - Data persistence handled by the backend (`InventoryMgt`).

- **Search and Filter**
  - Search items by model number, name, or category.
  - Filter items by selecting a category in the tree.

## Keyboard Shortcuts

- `Ctrl+N`: Add new item
- `F2`: Edit selected item
- `Delete`: Remove selected item
- `Ctrl+I`: Import data
- `Ctrl+E`: Export data
- `Ctrl+L`: View transaction log
- `F5`: Refresh view
- `Alt+X`: Exit application

## Requirements

- Java Development Kit (JDK) 8 or higher.
- 1024x600 minimum screen resolution.

## Usage

1. Ensure you have a JDK installed.
2. Run the `Main.java` file from your IDE.
3. The Swing GUI application will launch.
4. Use the category tree, search bar, table context menu, and menu bar to manage inventory.

## Project Structure Highlights

- `Main.java`: Entry point for launching the Swing GUI.
- `InventorySwingGUI.java`: Contains the Swing UI definition and event handling, interacting with `InventoryMgt`.
- `InventoryMgt.java`: Core backend logic for inventory, category, and transaction management.
- `Item.java`: Represents an inventory item.
- `Category.java`: Represents an item category.
- `Availability.java`: (Currently unused in the Swing GUI but remains in the backend).
- `Console.java`: (Commented out) Original console-based interface.
- `inventory_data.csv`: Default file for data persistence.
- `transaction_log.csv`: File for storing transaction history.

<!-- THINGS TO DO: (Leave this section for future tasks) -->
<!-- - Add proper icons to menu items and buttons. -->
<!-- - Implement more robust input validation in dialogs. -->
<!-- - Enhance the appearance and layout further. -->
<!-- - Consider multi-quantity items if required in the future. -->
<!-- - Improve error handling and user feedback. -->
<!-- - Add search/filter to the Transaction Log viewer. -->
<!-- - Explore better ways to handle category management visually. -->

## License

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.
