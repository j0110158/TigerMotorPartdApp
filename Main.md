# Main.java Class Overview

Here is a list of methods and the constructor found in the `Main` class:

*   **`Main()`**: The default constructor for the `Main` class (implicitly provided as no explicit constructor is defined). Initializes the `inventoryItems`, `itemCategories`, `scanner`, and `availabilityChecker` fields.
*   **`operations()`**: (public, void) - The main application loop and menu handler. Calls other methods based on user input.
*   **`addItemConsole()`**: (private, void) - Handles console interaction for adding a new item, collecting user input for item details.
*   **`removeItemConsole()`**: (private, void) - Handles console interaction for removing an item, prompting the user for removal criteria (number or category).
*   **`searchItemConsole()`**: (private, void) - Handles console interaction for searching items, prompting for search type and query.
*   **`manageInventoryConsole()`**: (private, void) - Handles console interaction for managing inventory, specifically editing item quantities.
*   **`manageCategoriesConsole()`**: (private, void) - Handles console interaction for managing categories (view, add, remove).
*   **`viewCategories()`**: (private, void) - Displays the current list of categories and their total quantities.
*   **`addCategoryConsole()`**: (private, void) - Handles console interaction for adding a new category.
*   **`removeCategoryConsole()`**: (private, void) - Handles console interaction for removing a category.
*   **`findItemByModelNumber(String modelNumber)`**: (private, Item) - Helper method to find an `Item` object in the `inventoryItems` list by its model number. Returns the `Item` object if found, otherwise `null`.
*   **`findCategoryByName(String categoryName)`**: (private, Category) - Helper method to find a `Category` object in the `itemCategories` list by its name. Returns the `Category` object if found, otherwise `null`.
*   **`addItem(double modelPrice, String modelName, String modelNumber, String itemCategory, int itemQuantity)`**: (public, void) - Creates a new `Item` object and adds it to the `inventoryItems` list, also updating the category quantity.
*   **`removeItemByNumber(String modelNumber)`**: (public, void) - Removes an item from `inventoryItems` based on its model number and updates the category quantity.
*   **`removeItemByCategory(String itemCategory)`**: (public, void) - Removes all items belonging to a specific category from `inventoryItems` and updates the category quantity.
*   **`searchItem(String query, String searchType)`**: (public, void) - Searches for items in the current inventory and saved items based on a query and search type (name, number, or category).
*   **`importData()`**: (public, void) - Reads inventory data from the `inventory.csv` file into the `inventoryItems` list.
*   **`exportData()`**: (public, void) - Writes the current inventory data from the `inventoryItems` list to the `inventory.csv` file.
*   **`addCategory(String categoryName)`**: (private, void) - Adds a new category to the `itemCategories` list if it doesn't already exist.
*   **`removeCategory(String categoryName)`**: (private, void) - Removes a category from the `itemCategories` list if it exists and has no associated items.
*   **`updateCategoryQuantity(String categoryName, int quantityChange)`**: (private, void) - Updates the total quantity for a given category in the `itemCategories` list, creating the category if it doesn't exist.
*   **`saveCategories()`**: (private, void) - Placeholder method for saving categories (currently noted as derived from inventory).
*   **`loadCategories()`**: (private, void) - Loads categories from the `categories.csv` file into the `itemCategories` list.
*   **`logTransaction(String action, String modelName, String modelNumber, int quantity)`**: (private, void) - Logs inventory actions to the `logbook.csv` file with a timestamp.
*   **`main(String[] args)`**: (public, static, void) - The entry point of the application. Creates a `Main` object and calls the `operations()` method to start the program. 