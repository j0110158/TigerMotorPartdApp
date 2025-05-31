import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator; // Import Iterator for safe removal
import java.util.Date; // Import Date for timestamping logs
import java.text.SimpleDateFormat; // Import SimpleDateFormat for date formatting
import java.util.stream.Collectors; // For stream operations

public class Main {

    // Collections to hold inventory items and categories
    // In a full app, these might be loaded from/saved to a file.
    private List<Item> inventoryItems = new ArrayList<>();
    // This list will store distinct Category objects to manage category-specific data like total quantity.
    private List<Category> itemCategories = new ArrayList<>();

    private Scanner scanner = new Scanner(System.in); // Use a single scanner object
    private Availability availabilityChecker = new Availability(); // Instance of Availability

    private static final String INVENTORY_FILE = "inventory.csv";
    private static final String SAVED_ITEMS_FILE = "saved_items.csv"; // Using the filename from Item.java
    private static final String LOGBOOK_FILE = "logbook.csv"; // File for transaction logs
    private static final String CATEGORIES_FILE = "categories.csv"; // File to save/load categories
    private static final String UNCATEGORIZED = "Uncategorized"; // Default category for items losing their category
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date format for logs

    // operations() - Main application loop and menu
    public void operations() {

        // Attempt to load data on startup
        loadCategories();
        importData();

        // Ensure the Uncategorized category exists
        addCategory(UNCATEGORIZED);

        boolean run = true;

        while (run) {
            System.out.println("\n--- Inventory Management Menu ---");
            System.out.println("1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Search Item");
            System.out.println("4. Import Inventory Data (.csv)");
            System.out.println("5. Export Inventory Data (.csv)");
            System.out.println("6. View Logbook");
            System.out.println("7. Manage Inventory");
            System.out.println("8. Manage Categories");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character after reading int

                switch (choice) {
                    case 1:
                        addItemConsole();
                        break;
                    case 2:
                        removeItemConsole();
                        break;
                    case 3:
                        searchItemConsole();
                        break;
                    case 4:
                        importData(); // Manual import of inventory
                        break;
                    case 5:
                        exportData(); // Export inventory
                        break;
                    case 6:
                        availabilityChecker.logBook(LOGBOOK_FILE);
                        break;
                    case 7:
                        manageInventoryConsole();
                        break;
                    case 8:
                        manageCategoriesConsole();
                        break;
                    case 9:
                        run = false;
                        System.out.println("Exiting program.");
                        exportData(); // Optional: Auto-save inventory on exit
                        saveCategories(); // Save categories on exit
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input to prevent infinite loop
            }
        }
        scanner.close(); // Close the scanner when the application exits
    }

    // Helper methods for console interaction to get user input
    private void addItemConsole() {
        System.out.println("--- Add Item ---");
        System.out.print("Enter Model Name: ");
        String modelName = scanner.nextLine();

        System.out.print("Enter Model Number: ");
        String modelNumber = scanner.nextLine();

        double modelPrice = -1; // Use a default invalid value
        while (modelPrice < 0) {
            System.out.print("Enter Model Price: ");
            if (scanner.hasNextDouble()) {
                modelPrice = scanner.nextDouble();
                scanner.nextLine(); // Consume the rest of the line
            } else {
                System.out.println("Invalid input. Please enter a valid price.");
                scanner.next(); // Consume invalid input
            }
        }

        int itemQuantity = -1; // Use a default invalid value
         while (itemQuantity < 0) {
            System.out.print("Enter Item Quantity: ");
            if (scanner.hasNextInt()) {
                itemQuantity = scanner.nextInt();
                scanner.nextLine(); // Consume the rest of the line
            } else {
                System.out.println("Invalid input. Please enter a valid integer quantity.");
                scanner.next(); // Consume invalid input
            }
        }

        System.out.print("Enter Item Category: ");
        String itemCategory = scanner.nextLine();

        addItem(modelPrice, modelName, modelNumber, itemCategory, itemQuantity);

        // Ask to save item details separately
        System.out.print("Save this item's details (Model, Name, Price, Quantity, Category) permanently? (yes/no): ");
        String saveChoice = scanner.nextLine();
        if (saveChoice.equalsIgnoreCase("yes")) {
            Item tempItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory); // Include category when saving
            tempItem.saveThisItem(); // Use the Item's save method
        }
    }

    private void removeItemConsole() {
         System.out.println("--- Remove Item ---");
         System.out.print("Remove by (Number/Category): ");
         String removeType = scanner.nextLine().toLowerCase();

         if (removeType.equals("number")) {
             System.out.print("Enter Model Number of item to remove: ");
             String modelNumber = scanner.nextLine();
             removeItemByNumber(modelNumber); // Call specific remove by number method
         } else if (removeType.equals("category")) {
              System.out.print("Enter Item Category to remove: ");
              String itemCategory = scanner.nextLine();
              removeItemByCategory(itemCategory); // Call specific remove by category method
         } else {
             System.out.println("Invalid removal type.");
         }
    }

    private void searchItemConsole() {
         System.out.println("--- Search Item ---");
         System.out.print("Search by (Name/Number/Category): ");
         String searchType = scanner.nextLine().toLowerCase();

         System.out.print("Enter search query: ");
         String query = scanner.nextLine();

         searchItem(query, searchType); // Pass query and search type
    }

    // manageInventoryConsole() - Handles console interaction for inventory editing
    private void manageInventoryConsole() {
        System.out.println("\n--- Manage Inventory ---");
        availabilityChecker.Inventory(INVENTORY_FILE); // Display current inventory

        System.out.print("Enter Model Number of item to edit quantity (or type 'back' to return): ");
        String modelNumberToEdit = scanner.nextLine();

        if (modelNumberToEdit.equalsIgnoreCase("back")) {
            return; // Go back to main menu
        }

        // Find the item in the inventoryItems list
        Item itemToEdit = findItemByModelNumber(modelNumberToEdit);

        if (itemToEdit != null) {
            int newQuantity = -1; // Use a default invalid value
            while (newQuantity < 0) {
                System.out.print("Enter New Quantity for " + itemToEdit.getModelName() + " (Current: " + itemToEdit.getItemQuantity() + "): ");
                if (scanner.hasNextInt()) {
                    newQuantity = scanner.nextInt();
                     scanner.nextLine(); // Consume the rest of the line
                } else {
                    System.out.println("Invalid input. Please enter a valid integer quantity.");
                    scanner.next(); // Consume invalid input
                }
            }

            int oldQuantity = itemToEdit.getItemQuantity();
            itemToEdit.setItemQuantity(newQuantity); // Update quantity in the item object
            System.out.println("Quantity for " + itemToEdit.getModelName() + " updated from " + oldQuantity + " to " + newQuantity + ".");

            // Update category quantity in the itemCategories list
            updateCategoryQuantity(itemToEdit.getItemCategory(), newQuantity - oldQuantity); // Pass the quantity change

            logTransaction("EDIT_QUANTITY", itemToEdit.getModelName(), itemToEdit.getModelNumber(), newQuantity); // Log the quantity edit

            // Optional: Offer to save changes after editing
            System.out.print("Save changes to inventory file? (yes/no): ");
            String saveChoice = scanner.nextLine();
            if (saveChoice.equalsIgnoreCase("yes")) {
                exportData(); // Export the updated inventory to the file
            }

        } else {
            System.out.println("Item with Model Number " + modelNumberToEdit + " not found in current inventory.");
        }
    }

    // manageCategoriesConsole() - Handles console interaction for category management
    private void manageCategoriesConsole() {
        System.out.println("\n--- Manage Categories ---");
        System.out.println("1. View Categories");
        System.out.println("2. Add Category");
        System.out.println("3. Remove Category");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");

        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewCategories();
                    break;
                case 2:
                    addCategoryConsole();
                    break;
                case 3:
                    removeCategoryConsole();
                    break;
                case 4:
                    return; // Go back
                default:
                    System.out.println("Invalid choice.");
            }
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume invalid input
        }
         // Loop back to category menu after an action
        manageCategoriesConsole();
    }

    private void viewCategories() {
        System.out.println("--- Available Categories ---");
        if (itemCategories.isEmpty()) {
            System.out.println("No categories defined.");
            return;
        }
        // Sort categories by name for consistent display
        itemCategories.stream()
                      .sorted((c1, c2) -> c1.getCategoryName().compareToIgnoreCase(c2.getCategoryName()))
                      .forEach(category -> System.out.println("Category: " + category.getCategoryName() + ", Total Quantity: " + category.getCategoryQuantity()));
    }

    private void addCategoryConsole() {
         System.out.println("--- Add Category ---");
         System.out.print("Enter New Category Name: ");
         String categoryName = scanner.nextLine();
         addCategory(categoryName);
    }

    private void removeCategoryConsole() {
         System.out.println("--- Remove Category ---");
         System.out.print("Enter Category Name to Remove: ");
         String categoryName = scanner.nextLine();
         removeCategory(categoryName);
    }

    // Helper method to find an item by model number in the inventoryItems list
    private Item findItemByModelNumber(String modelNumber) {
        for (Item item : inventoryItems) {
            if (item.getModelNumber().equalsIgnoreCase(modelNumber)) {
                return item;
            }
        }
        return null; // Item not found
    }

    // Helper method to find a category by name in the itemCategories list
    private Category findCategoryByName(String categoryName) {
        for (Category category : itemCategories) {
             if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                 return category;
             }
        }
        return null; // Category not found
    }

    // addItem(input:ModelPrice, ModelName, ModelNumber, itemCategory, itemQuantity)
    // This method will create a new Item and add it to the inventoryItems list.
    public void addItem(double modelPrice, String modelName, String modelNumber, String itemCategory, int itemQuantity) {
        // Create a new Item object and add it to the inventoryItems list
        // Corrected order of arguments for the Item constructor
        Item newItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
        inventoryItems.add(newItem);

        System.out.println("Item added: " + modelName + " (" + modelNumber + ").");

        // Update the total quantity for the category
        updateCategoryQuantity(itemCategory, itemQuantity); // This helper handles finding/creating category
    }

    // removeItemByNumber(input:ModelNumber)
    // This method removes an item based on its model number.
    public void removeItemByNumber(String modelNumber) {
        Iterator<Item> iterator = inventoryItems.iterator();
        boolean foundAndRemoved = false;

        while (iterator.hasNext()) {
            Item item = iterator.next();

            if (item.getModelNumber().equalsIgnoreCase(modelNumber)) {
                int removedQuantity = item.getItemQuantity(); // Get quantity before removing
                String removedCategory = item.getItemCategory(); // Get category before removing

                iterator.remove(); // Safe removal using iterator
                System.out.println("Item with Model Number " + modelNumber + " removed.");
                foundAndRemoved = true;

                // Update category quantity
                updateCategoryQuantity(removedCategory, -removedQuantity); // Decrease category quantity

                logTransaction("REMOVE", item.getModelName(), item.getModelNumber(), removedQuantity); // Log removing the quantity that was in stock
                break; // Assuming only one item per model number for now
            }
        }

        if (!foundAndRemoved) {
             System.out.println("Item with Model Number " + modelNumber + " not found.");
        }
    }

    // removeItemByCategory(input:itemCategory)
    // This method removes all items belonging to a specific category.
    public void removeItemByCategory(String itemCategory) {
        Iterator<Item> iterator = inventoryItems.iterator();
        boolean foundAndRemoved = false;
        int totalRemovedQuantity = 0;

        // Find the category first to check existence and get the Category object
        Category categoryToRemove = findCategoryByName(itemCategory);

        if (categoryToRemove == null) {
             System.out.println("Category '" + itemCategory + "' not found.");
             return;
        }

        // Create a temporary list of items to remove to avoid ConcurrentModificationException
        List<Item> itemsToRemove = new ArrayList<>();
        for (Item item : inventoryItems) {
            if (item.getItemCategory().equalsIgnoreCase(itemCategory)) {
                itemsToRemove.add(item);
            }
        }

        if (itemsToRemove.isEmpty()) {
             System.out.println("No items found in category " + itemCategory + ".");
             return;
        }

        // Remove items and update quantities
        for (Item item : itemsToRemove) {
            int removedQuantity = item.getItemQuantity();
            inventoryItems.remove(item); // Remove item from the main list
            totalRemovedQuantity += removedQuantity;
            System.out.println("Removed item: " + item.getModelName() + " (Quantity: " + removedQuantity + ")");
            logTransaction("REMOVE", item.getModelName(), item.getModelNumber(), removedQuantity); // Log each item removal
            foundAndRemoved = true; // At least one item was found and removed
        }

        // Update category quantity in the itemCategories list
        if (foundAndRemoved) {
            System.out.println("All items in category '" + itemCategory + "' removed. Total quantity removed: " + totalRemovedQuantity);
            updateCategoryQuantity(itemCategory, -totalRemovedQuantity); // Decrease category quantity by total removed

             // Check if the category is now empty and remove it if desired
             if (categoryToRemove.getCategoryQuantity() <= 0) {
                  // Check again to be safe after quantity update
                   long itemCountInCategoryAfterRemoval = inventoryItems.stream()
                                                                       .filter(item -> item.getItemCategory().equalsIgnoreCase(itemCategory))
                                                                       .count();
                   if (itemCountInCategoryAfterRemoval == 0) {
                        removeCategory(itemCategory); // Remove the category object
                         System.out.println("Category '" + itemCategory + "' is now empty and has been removed.");
                   } else {
                       // This case indicates an issue if updateCategoryQuantity is correct
                        System.err.println("Warning: Category quantity for '" + itemCategory + "' is 0 or less, but items still exist.");
                   }
             }
        }
        // If no items were found (itemsToRemove was empty), the initial check already handled it.
    }

    // searchItem(input:ModelName OR Model Numer OR itemCategory)
    // This method will search for an item based on name, number, or category.
    // It should display details of saved items even if not currently in stock.
    public void searchItem(String query, String searchType) {
        boolean foundInInventory = false;
        boolean foundInSaved = false;

        // Search current inventoryItems
        System.out.println("Searching in current inventory...");
        for (Item item : inventoryItems) {
            boolean matches = false;
            if (searchType.equals("name") && item.getModelName().equalsIgnoreCase(query)) {
                matches = true;
            } else if (searchType.equals("number") && item.getModelNumber().equalsIgnoreCase(query)) {
                matches = true;
            } else if (searchType.equals("category") && item.getItemCategory().equalsIgnoreCase(query)) { // Check against the single itemCategory attribute
                 matches = true;
            }

            if (matches) {
                System.out.println("--- Item Found (In Inventory) ---");
                System.out.println(availabilityChecker.getDetails(item.getModelName(), item.getModelPrice(), item.getItemCategory())); // Pass actual category
                System.out.println("Quantity in stock: " + item.getItemQuantity());
                availabilityChecker.statusItem(item.getItemQuantity());
                foundInInventory = true;
                // Continue searching to find all matches in inventory
            }
        }

         // Implement searching in the saved_items.csv file.
         System.out.println("Searching in saved items...");
         try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_ITEMS_FILE))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 String[] parts = line.split(",");
                 // Expecting at least modelNumber,modelName,modelPrice,itemQuantity,itemCategory
                 if (parts.length >= 5) {
                      String modelNumber = parts[0].trim();
                      String modelName = parts[1].trim();
                      String itemCategory = parts[4].trim(); // Category is the 5th part

                      boolean matches = false;
                      if (searchType.equals("name") && modelName.equalsIgnoreCase(query)) {
                          matches = true;
                      } else if (searchType.equals("number") && modelNumber.equalsIgnoreCase(query)) {
                          matches = true;
                      } else if (searchType.equals("category") && itemCategory.equalsIgnoreCase(query)) {
                           matches = true;
                      }

                      if (matches) {
                           System.out.println("--- Item Found (Saved) ---");
                           // Display saved item details.
                           String savedDetails = "Details: Name=" + modelName + ", Number=" + modelNumber + ", Price=" + parts[2].trim();
                           if (parts.length >= 4) {
                               // Assuming quantity is present in the saved items file format
                               savedDetails += ", Saved Quantity: " + parts[3].trim();
                           }
                            savedDetails += ", Category: " + itemCategory; // Include category from saved file
                           System.out.println(savedDetails);
                           foundInSaved = true;
                           // Continue searching to find all saved matches
                      }
                 } else {
                      // Handle lines in saved_items.csv with fewer than 5 parts if necessary,
                      // depending on whether older saved formats need to be supported.
                      System.err.println("Skipping invalid line format in " + SAVED_ITEMS_FILE + ": " + line + " (Expected at least 5 fields)");
                 }
             }
         } catch (IOException e) {
             System.out.println("No saved items file found or error reading saved items: " + e.getMessage());
             // TODO: More robust error handling.
         } catch (NumberFormatException e) {
              System.err.println("Error parsing data in saved items file: " + e.getMessage());
              // TODO: More robust error handling.
         }

        if (!foundInInventory && !foundInSaved) {
            System.out.println("No item found matching '" + query + "' in current inventory or saved items.");
        }
    }

    // importData (.csv)
    // This method will read item data from inventory.csv file.
    // Updated to read itemQuantity and handle potential category.
    public void importData() {
        System.out.println("Attempting to import data from " + INVENTORY_FILE + "...");
        inventoryItems.clear(); // Clear current inventory before importing
        // Categories are now loaded from a separate file, so we don't clear them here.
        // itemCategories.clear(); // No longer clearing categories here

        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Updated to expect at least 4 parts (number, name, price, quantity) and optionally 5 (category)
                if (parts.length >= 4) {
                    try {
                        String modelNumber = parts[0].trim();
                        String modelName = parts[1].trim();
                        double modelPrice = Double.parseDouble(parts[2].trim());
                        int itemQuantity = Integer.parseInt(parts[3].trim());
                        String itemCategory = (parts.length >= 5) ? parts[4].trim() : "Uncategorized"; // Read category if present

                        Item importedItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                         // TODO: Associate the imported item with its category properly.
                         // For now, we add the item and update/add the category based on the item's category string.
                        inventoryItems.add(importedItem);
                        updateCategoryQuantity(itemCategory, itemQuantity); // Update category quantity on import

                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line format in " + INVENTORY_FILE + ": " + line + " (Number format error)");
                        // TODO: More robust error logging
                    } catch (IllegalArgumentException e) { // Catch potential errors from Item constructor if validation is added
                         System.err.println("Skipping invalid item data in " + INVENTORY_FILE + ": " + line + " (" + e.getMessage() + ")");
                          // TODO: More robust error logging
                    }
                } else {
                     System.err.println("Skipping invalid line format in " + INVENTORY_FILE + ": " + line + " (Incorrect number of fields)");
                     // TODO: More robust error logging
                }
            }
            System.out.println("Data imported successfully from " + INVENTORY_FILE + ". Total items: " + inventoryItems.size() + ", Total categories: " + itemCategories.size());
        } catch (IOException e) {
            System.out.println("No existing " + INVENTORY_FILE + " found or error reading file. Starting with empty inventory.");
            // TODO: More robust error handling/logging
        }
         // Categories are now inferred and updated during inventory import.
         // If categories were stored separately, we would load them here.
    }

    // exportData (.csv)
    // This method will write current inventory data to inventory.csv file.
    // Updated to write itemQuantity and category.
    public void exportData() {
        System.out.println("Exporting data to " + INVENTORY_FILE + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            // Write header (optional)
            // writer.println("ModelNumber,ModelName,ModelPrice,ItemQuantity,ItemCategory");

            for (Item item : inventoryItems) {
                // CSV format: modelNumber,modelName,modelPrice,itemQuantity,itemCategory
                writer.println(item.getModelNumber() + "," + item.getModelName() + "," + item.getModelPrice() + "," + item.getItemQuantity() + "," + item.getItemCategory());
            }
            System.out.println("Data exported successfully to " + INVENTORY_FILE + ".");
        } catch (IOException e) {
            System.err.println("Error exporting data to file: " + e.getMessage());
            // TODO: Implement more robust error handling.
        }
         // Categories are saved implicitly as part of item data for now.
         // If categories had additional attributes to save, a separate saveCategories method would be needed.
    }

    // Helper method to add a new category if it doesn't exist
    private void addCategory(String categoryName) {
        // Allow the UNCATEGORIZED category name, only check for null or empty.
        if (categoryName == null || categoryName.trim().isEmpty()) {
             System.out.println("Invalid category name.");
             return;
        }
        if (findCategoryByName(categoryName) == null) {
            Category newCategory = new Category(categoryName);
            itemCategories.add(newCategory);
            System.out.println("Category '" + categoryName + "' added.");
        } else {
            System.out.println("Category '" + categoryName + "' already exists.");
        }
    }

    // Helper method to remove a category by name
    private void removeCategory(String categoryName) {
        if (categoryName.equalsIgnoreCase(UNCATEGORIZED)) {
             System.out.println("Cannot remove the default '" + UNCATEGORIZED + "' category.");
             return;
        }

        Iterator<Category> iterator = itemCategories.iterator();
        boolean foundAndRemoved = false;
        while (iterator.hasNext()) {
            Category category = iterator.next();
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                 // Check if there are items still associated with this category
                 long itemCountInCategory = inventoryItems.stream()
                                                        .filter(item -> item.getItemCategory().equalsIgnoreCase(categoryName))
                                                        .count();
                 if (itemCountInCategory > 0) {
                      System.out.println("Cannot remove category '" + categoryName + "': " + itemCountInCategory + " items are still assigned to this category. Please reassign or remove these items first.");
                 } else {
                    iterator.remove();
                    System.out.println("Category '" + categoryName + "' removed.");
                    foundAndRemoved = true;
                 }
                 break; // Assuming one Category object per conceptual category name
            }
        }
        if (!foundAndRemoved) {
            System.out.println("Category '" + categoryName + "' not found.");
        }
    }

    // Helper method to update the total quantity for a category
    private void updateCategoryQuantity(String categoryName, int quantityChange) {
        // Ensure categoryName is not null or empty, default to Uncategorized if it is.
        String effectiveCategoryName = (categoryName == null || categoryName.trim().isEmpty()) ? UNCATEGORIZED : categoryName;

        Category categoryToUpdate = findCategoryByName(effectiveCategoryName);
        if (categoryToUpdate == null) {
            // If category doesn't exist, create it (e.g., on importing an item with a new category)
            categoryToUpdate = new Category(effectiveCategoryName);
            itemCategories.add(categoryToUpdate);
             System.out.println("Auto-created category '" + effectiveCategoryName + "' due to item association.");
        }
        // Update the quantity and trigger the alert if needed within the Category object
        categoryToUpdate.setCategoryQuantity(categoryToUpdate.getCategoryQuantity() + quantityChange);
    }

    // Placeholder methods for saving/loading categories if they had more complex data
    private void saveCategories() {
        // Current categories are derived from inventory items. If categories had unique attributes,
        // this method would save the itemCategories list to a separate file (e.g., categories.csv).
        System.out.println("Categories saved (currently derived from inventory).");
         // TODO: Implement actual saving if Category class grows.
    }

    private void loadCategories() {
        // Current categories are inferred during inventory import. If categories had unique attributes,
        // this method would load the itemCategories list from a separate file (e.g., categories.csv).
        System.out.println("Attempting to load categories from " + CATEGORIES_FILE + "...");
        itemCategories.clear(); // Clear current categories before loading

        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Expecting 2 parts: categoryName,categoryQuantity
                if (parts.length == 2) {
                    try {
                        String categoryName = parts[0].trim();
                        int categoryQuantity = Integer.parseInt(parts[1].trim());

                        Category category = new Category(categoryName);
                        category.setCategoryQuantity(categoryQuantity); // Set the loaded quantity
                        itemCategories.add(category);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line format in " + CATEGORIES_FILE + ": " + line + " (Quantity is not a valid number)");
                        // TODO: More robust error logging
                    }
                } else {
                     System.err.println("Skipping invalid line format in " + CATEGORIES_FILE + ": " + line + " (Expected 2 fields)");
                     // TODO: More robust error logging
                }
            }
            System.out.println("Category data loaded successfully from " + CATEGORIES_FILE + ". Total categories: " + itemCategories.size());
        } catch (IOException e) {
            System.out.println("No existing " + CATEGORIES_FILE + " found or error reading file. Starting with empty categories.");
            // TODO: More robust error handling/logging
        }
    }

    // logTransaction(action, modelName, modelNumber, quantity)
    // Logs inventory actions to the logbook file.
    private void logTransaction(String action, String modelName, String modelNumber, int quantity) {
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOGBOOK_FILE, true))) { // Append to log file
            String timestamp = dateFormat.format(new Date());
            // Log format: timestamp,action,modelName,modelNumber,quantity
            logWriter.println(timestamp + "," + action + "," + modelName + "," + modelNumber + "," + quantity);
        } catch (IOException e) {
            System.err.println("Error writing to logbook file: " + e.getMessage());
            // TODO: Implement more robust error handling.
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.operations();
    }

    // TODO: Add methods for global error handling or utility functions if needed.
}

