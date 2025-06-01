# Java File Overview

## File Structure and Features

### `Main.java`
- **Purpose**: Entry point of the application
- **Features**:
  - Initializes the application
  - Calls Console.start() to begin the program
  - Simple and focused responsibility

### `Console.java`
- **Purpose**: Handles user interaction and program flow
- **Features**:
  - **Main Menu System**
    - Display main menu with options
    - View current inventory
    - View transaction logs
    - Add new items
    - Remove items
    - Search for items
    - Manage categories

  - **Item Management**
    - Add items with validation for:
      * Model number
      * Model name
      * Price
      * Quantity
      * Category
    - Remove items by:
      * Model number
      * Category
    - Search items with:
      * Detailed item information
      * Availability status
      * Price information
      * Category information

  - **Category Management**
    - View all categories
    - Add new categories
    - Remove categories
    - Update category quantities
    - View category details

  - **Input Validation**
    - String input validation
    - Numeric input validation
    - Price input validation
    - Confirmation prompts
    - Error handling

### `Availability.java`
- **Purpose**: Manages file operations and item status
- **Features**:
  - **Status Checking**
    - Check item availability
    - Display availability status
    - Show restock warnings

  - **File Operations**
    - Read transaction logs
    - Read inventory data
    - Import data from files
    - Export data to files
    - Log transactions with:
      * Timestamps
      * Action type
      * Item details
      * Quantity changes

  - **Data Validation**
    - Validate item details
    - Check file existence
    - Handle file errors
    - Validate imported data

### `InventoryMgt.java`
- **Purpose**: Core inventory management system
- **Features**:
  - **Inventory Management**
    - Store inventory items
    - Add new items
    - Remove items
    - Search items
    - Update item quantities
    - Track item categories

  - **Category Management**
    - Store categories
    - Add new categories
    - Remove categories
    - Update category quantities rather - Track category quantity
    - View category details
    - Track category quantities

  - **Data Validation**
    - Validate item data
    - Validate category data
    - Check for duplicates
    - Prevent negative quantities
    - Handle errors

### `Category.java`
- **Purpose**: Manages category data and behavior
- **Features**:
  - **Category Data**
    - Store category name
    - Store category quantity
    - Track restock status

  - **Quantity Management**
    - Increase quantity
    - Decrease quantity
    - Check restock needs
    - Prevent negative quantities
    - Show restock warnings

  - **Data Validation**
    - Validate category name
    - Validate quantities
    - Check restock threshold

### `Item.java`
- **Purpose**: Manages item data and behavior
- **Features**:
  - **Item Data**
    - Store item details:
      * Model price
      * Model name
      * Model number
      * Item quantity
      * Item category

  - **Quantity Management**
    - Increase quantity
    - Decrease quantity
    - Prevent negative quantities

  - **Data Validation**
    - Validate price
    - Validate name
    - Validate number
    - Validate quantity
    - Validate category

## Common Features Across All Files

### 1. Error Handling
- Input validation
- File operation errors
- Data validation
- Exception handling

### 2. Data Management
- CRUD operations
- Data persistence
- Data validation
- State management

### 3. User Interface
- Menu system
- Input prompts
- Status messages
- Confirmation dialogs

### 4. File Operations
- Reading files
- Writing files
- Import/Export
- Logging

### 5. Business Logic
- Inventory tracking
- Category management
- Quantity control
- Status monitoring

### 6. Security Features
- Input validation
- Data validation
- Error prevention
- Safe file operations (.xlsx)

2. Enhanced error handling and validation across all files
3. Improved file operation security
4. Added comprehensive documentation
5. Streamlined user interface
6. Enhanced data management capabilities

These changes address the primary errors observed and improve the initial startup behavior related to categories. 

---

## .java vs .class Files

In Java, you work with two main types of files:

*   **`.java` files:** These contain the **source code** you write. It's human-readable text that defines your classes, methods, and program logic.

*   **`.class` files:** These contain **bytecode**. When you compile your `.java` files using a Java compiler (like `javac`), the source code is translated into this platform-independent format. Bytecode is not easily human-readable.

**Purpose:**

The distinction is crucial for Java's **platform independence**. You write your source code once in a `.java` file. The compiler converts it to `.class` bytecode, which is the same across different operating systems. Then, the Java Virtual Machine (JVM) on each specific operating system interprets and executes this bytecode. This allows your Java program to run on any device with a compatible JVM without needing to be rewritten or recompiled for each specific platform. 

## Recent Changes

### Item Class Modifications
- Removed the `saveThisItem` method that was responsible for saving individual item details to a CSV file
- Removed quantity validation methods:
  - `increaseQuantity`: Previously handled positive quantity increases
  - `decreaseQuantity`: Previously handled quantity decreases with zero floor validation
- Kept the basic item management functionality including:
  - Attributes (modelPrice, modelName, modelNumber, itemQuantity, itemCategory)
  - Constructor
  - Getters and setters for all attributes

These changes simplify the Item class to focus on core item management functionality without automatic file saving or quantity validation.

## New Changes Adopted

- **Consolidated File Management:** The application now uses a single `inventory_data.csv` file to store all inventory items, categories, and transaction logs. This simplifies file handling compared to using multiple separate CSV files.
- **Structured CSV Format:** The `inventory_data.csv` file uses prefixes (`CATEGORY,`, `ITEM,`, `LOG,`) to identify different types of data records, making the file human-readable and enabling the program to parse the combined data correctly.
- **Refactored File I/O Logic:** The `InventoryMgt` class has been refactored to contain all the logic for reading from and writing to the single combined CSV file.
- **Distinct File Operations:** New methods `importData(filePath)`, `exportData(filePath)`, and `saveData()` in `InventoryMgt` provide clear functionalities for loading data from a specified file, exporting current data to a specified file, and saving data to the default file, respectively.
- **Interactive Category Creation:** When adding a new item, if the entered category does not exist, the user is now prompted to create it. If confirmed, the category is added and saved along with the new item.
- **Display Existing Categories:** When adding an item and entering a category that already exists, the program now displays the list of current categories as a reference.
