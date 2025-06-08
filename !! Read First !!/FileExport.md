# Data Management in Tiger Motorhub App

This document outlines how data is managed and persisted within the Tiger Motorhub application, following recent updates.

## Data Persistence

The application automatically saves and loads inventory data and transaction logs to and from a CSV file. This ensures that your inventory state is preserved across application sessions.

- **Automatic Saving**: All changes to the inventory (adding, removing, or editing items) and new transactions are automatically saved.
- **Automatic Loading**: Upon application startup, the most recently configured data file is loaded to restore the previous inventory state.

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