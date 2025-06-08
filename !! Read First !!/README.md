# Tiger Motorhub App - Inventory Management System

Desktop inventory management application (Java Swing).

## Key Features

### 1. Intuitive User Interface (GUI)
*   **Modern Design**: Clean Swing UI.
*   **Split Pane Layout**: Categories and items side-by-side.
*   **Search and Filter**: Search/filter by model, name, category.
*   **Right-click Context Menu**: Item edit/remove access.
*   **Status Bar**: Real-time feedback.

### 2. Comprehensive Inventory Management
*   **Add Items**: Dialog for new items.
*   **Edit Items**: Modify existing items.
*   **Remove Items**: Delete items with robust error handling.
    *   **Fix for "Error removing item: Amount cannot be negative"**: Corrected quantity logic.
*   **Backend Logic**: Uses `InventoryMgt`.
*   **Fixed Item Quantity**: Quantity fixed at 1.

### 3. Smart Stock Management
*   **Low Stock Notifications**: Real-time low stock alerts.
*   **Configurable Threshold**: User-set low stock quantity via Settings.

### 4. Financial Overview
*   **Cumulative Worth**: Displays total inventory value.

### 5. Robust Data Management
*   **Automatic Data Persistence**: Inventory/logs saved to `inventory_data.csv`.
*   **Configurable Data Path**: User sets `inventory_data.csv` location.
*   **Transaction Logging**: Detailed action log via viewer.
*   **No Explicit Import/Export**: Automatic management only.

### 6. Category Management
*   **Hierarchical Tree View**: Organized categories.
*   **Automatic Creation**: Categories created on item addition.
*   **Filtering**: Filter items by selected category.

## Application Features

- Settings Menu: Access options for low stock threshold and data folder path.
- Inventory Management: Add, edit, and remove items.
- Search & Filter: Find items by various criteria and filter by category.
- Restock Alerts: Notifications for low-stock items.
- Transaction Log: View a history of inventory operations.
- **New UI Theme**: Implemented a modern, consistent dark color palette using `UIManager` properties to ensure a cohesive look.

## Keyboard Shortcuts

- `Ctrl+N`: Add item
- `F2`: Edit item
- `Delete`: Remove item
- `Ctrl+L`: View log
- `F5`: Refresh view
- `Alt+X`: Exit app

## Requirements

- Java Development Kit (JDK) 8+.
- 1024x600 minimum resolution.

## Usage

1.  Install JDK.
2.  Run `Main.java` from IDE.
3.  GUI launches.
4.  Manage inventory via UI.

## Project Structure Highlights

- `Main.java`: App entry point.
- `InventorySwingGUI.java`: Main GUI.
- `InventoryMgt.java`: Core business logic.
- `Item.java`: Item data model.
- `Category.java`: Category data model.
- `Availability.java`: Data persistence.
- `Console.java`: (Commented out) Original CLI.
- `inventory_data.csv`: Primary data file.
- `logbook.csv`: Legacy log file.

## License

Proprietary and confidential.
