# TigerMotorPartdApp
Tiger Motor Parts App (Inventory Management System)

THINGS TO DO TOMORROW (June 1, 2025): 
* READ THE CODE 
    Availability.java 
    Category.java
    Console.java 
    InventoryMgt.java 
    Item.java 
    Main.java
* Remove itemQuantity and their validations, only consider validating categoryQuantity
* Transition from .csv to .xlsx for better file import/exporting, because what is seen is that there are many .csv files used, but ideally it should be ONE file only for import and exporting 
* Consider the new Figma prototypes tomorrow, such as overlay features for addItem with their attributes, managing categories, and switch to JavaFX and Swing if its possible sana

## Key Features

- **Console-based Interface:** Interact with the inventory system through a command-line console.
- **Inventory Management:** Add, remove, and search for motor parts items.
- **Category Management:** Organize items into categories, with automatic quantity tracking at the category level.
- **Transaction Logging:** Records actions performed within the system.
- **Single File Persistence:** All data (inventory, categories, logs) is stored and managed in a single, human-readable CSV file (`inventory_data.csv`).
- **Dynamic File Handling:** The program checks for the data file on startup and prompts the user to create it if missing.

## New Changes Adopted

- **Consolidated File Management:** The application now uses a single `inventory_data.csv` file to store all inventory items, categories, and transaction logs. This simplifies file handling compared to using multiple separate CSV files.
- **Structured CSV Format:** The `inventory_data.csv` file uses prefixes (`CATEGORY,`, `ITEM,`, `LOG,`) to identify different types of data records, making the file human-readable and enabling the program to parse the combined data correctly.
- **Refactored File I/O Logic:** The `InventoryMgt` class has been refactored to contain all the logic for reading from and writing to the single combined CSV file.
- **Distinct File Operations:** New methods `importData(filePath)`, `exportData(filePath)`, and `saveData()` in `InventoryMgt` provide clear functionalities for loading data from a specified file, exporting current data to a specified file, and saving data to the default file, respectively.
- **Interactive Category Creation:** When adding a new item, if the entered category does not exist, the user is now prompted to create it. If confirmed, the category is added and saved along with the new item.
- **Display Existing Categories:** When adding an item and entering a category that already exists, the program now displays the list of current categories as a reference.
- **Dynamic File Creation on Startup:** The program now checks for the existence of `inventory_data.csv` on startup and prompts the user to create it if it's missing.
