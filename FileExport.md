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