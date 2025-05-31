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

# Recent Changes and Features Documentation

## File Structure and Features

### 1. Main.java
- **Purpose**: Entry point of the application
- **Features**:
  - Initializes the application
  - Calls Console.start() to begin the program
  - Simple and focused responsibility

### 2. Console.java
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

### 3. Availability.java
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

### 4. InventoryMgt.java
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

### 5. Category.java
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

### 6. Item.java
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
