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
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import java.awt.BorderLayout;
import java.awt.Color;

public class Main {

    public static void main(String[] args) {
        Main app = new Main();
        app.operations();
    }
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
        // It should call an Object through Polymorphism
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
    

    // logTransaction(action, modelName, modelNumber, quantity)
    // Logs inventory actions to the logbook file.
    
    

    // TODO: Add methods for global error handling or utility functions if needed.
}
