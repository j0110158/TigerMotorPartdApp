# File Export Documentation

## CSV to XLSX Transition Discussion

Discussed the potential to move from using multiple `.csv` files (for inventory, logbook, categories, etc.) to a single `.xlsx` file for all data import and export.

**Goal:** Simplify file management for the user by consolidating all data into one file with potentially different sheets.

**Complexity:** This transition is complex because it requires:
1.  Adding an external Java library (like Apache POI) to handle the `.xlsx` format.
2.  Completely rewriting the file reading and writing logic to work with the structure of Excel files (sheets, rows, columns).

The user has decided not to proceed with the implementation of this change at this time to further consider the approach.

## Consolidation to a Single CSV

As an alternative to transitioning to XLSX, the plan is now to consolidate all data (inventory, logbook, categories) into a single `.csv` file.

**Goal:** Simplify file management for the user by having only one CSV file for import/export, avoiding the need for external libraries like Apache POI.

**Implemented Approach:** The application now uses a single `inventory_data.csv` file with distinct record types (`CATEGORY,`, `ITEM,`, `LOG,`) to store all persistent data. File handling is centralized in the `InventoryMgt` class with dedicated import, export, and save methods. Dynamic file creation on startup is also implemented.

# Data Import/Export

This document explains the choice of using CSV files for data import and export in the Tiger Motor Parts Inventory Management System, and details the structure of the `inventory_data.csv` file.

## Why CSV over XLSX?

While XLSX (Excel spreadsheet) files are common for data storage and manipulation due to their रिच (rich) formatting and multiple sheets, we chose CSV (Comma Separated Values) for this application for several key reasons:

1.  **Simplicity and Parsability:** CSV is a plain text format that is straightforward to read and write programmatically. Parsing CSV data is significantly simpler than parsing the complex structure of an XLSX file, which would typically require external libraries and more intricate code.
2.  **Compatibility:** CSV is a widely compatible format that can be opened and processed by almost any spreadsheet software (like Excel, Google Sheets, LibreOffice Calc), plain text editors, and various programming languages and tools. This ensures the data can be easily accessed and manipulated outside of this specific Java application.
3.  **Performance:** For simple tabular data like our inventory, categories, and logs, CSV offers better performance in terms of reading and writing speed compared to the overhead of processing the more complex XLSX format.
4.  **Backend Focus:** Given that the core backend logic is implemented in Java, working with a simple text-based format like CSV aligns well with typical file handling in Java without needing heavy external dependencies specifically for spreadsheet manipulation.
5.  **Single File vs. Multiple:** Although our initial thought might have involved using multiple CSV files, the current implementation in `InventoryMgt.java` efficiently handles reading and writing all necessary data (Categories, Items, Logs) from and to a *single* structured CSV file (`inventory_data.csv`), which simplifies file management while retaining the benefits of CSV.

## `inventory_data.csv` Structure

The `inventory_data.csv` file is a single CSV file that stores all three types of data (Categories, Items, and Transaction Logs) in a structured format. Each line in the file represents a single record, prefixed by a type identifier to distinguish between different data entries.

The structure uses a comma (`,`) as the delimiter.

Here's the format for each record type:

1.  **Categories:**
    -   Prefix: `CATEGORY`
    -   Format: `CATEGORY,categoryName,categoryQuantity`
    -   Example: `CATEGORY,Electronics,15`

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

This combined format allows the `importData` and `exportData` methods in `InventoryMgt.java` to read and write all application data to and from a single, easily parsable file. 