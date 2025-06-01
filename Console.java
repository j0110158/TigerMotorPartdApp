import java.util.Scanner;
import java.util.List;

public class Console {
    private static Scanner scanner = new Scanner(System.in);
    private static InventoryMgt inventoryManager = new InventoryMgt();
    private static Availability availabilityChecker = new Availability();

    public static void start() {
        System.out.println("Welcome to Tiger Motor Parts Inventory Management System!");
        System.out.println("Initializing data...");
        
        try {
            // Dynamic check and creation of the data file
            if (!inventoryManager.dataFileExists()) {
                System.out.println("Data file '" + InventoryMgt.DATA_FILE + "' not found.");
                boolean createNewFile = confirmAction("Do you want to create a new empty data file ('" + InventoryMgt.DATA_FILE + "')?");
                if (createNewFile) {
                    if (inventoryManager.createDataFile()) {
                        System.out.println("Proceeding with data import.");
                    } else {
                        System.out.println("Failed to create data file. Data will not be saved persistently.");
                    }
                } else {
                    System.out.println("Data file not created. Data will not be saved persistently.");
                }
            } else {
                 System.out.println("Data file '" + InventoryMgt.DATA_FILE + "' found.");
            }

            // Load all data from the combined CSV file using InventoryMgt
            // importData is designed to handle empty or non-existent files gracefully
            inventoryManager.importData(InventoryMgt.DATA_FILE);
            // loadCategories() is no longer needed as categories are loaded with importData
            
            while (true) {
                displayMenu();
                int choice = getIntInput("Enter your choice: ");
                
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
                        manageCategoriesConsole();
                        break;
                    case 5:
                        viewInventoryConsole();
                        break;
                    case 6:
                        viewTransactionLogConsole();
                        break;
                    case 7:
                        if (confirmAction("Are you sure you want to exit?")) {
                            System.out.println("Thank you for using Tiger Motor Parts Inventory Management System!");
                            // Optionally save data on exit
                            inventoryManager.saveData();
                            return;
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Tiger Motor Parts Inventory Management System ===");
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. Search Item");
        System.out.println("4. Manage Categories");
        System.out.println("5. View Inventory");
        System.out.println("6. View Transaction Log");
        System.out.println("7. Exit");
        System.out.println("=================================================");
    }

    private static void addItemConsole() {
        System.out.println("\n=== Add New Item ===");
        String modelNumber = getStringInput("Enter model number: ", true);
        String modelName = getStringInput("Enter model name: ", true);
        double modelPrice = getDoubleInput("Enter model price: ", 0.0);
        int itemQuantity = getIntInput("Enter item quantity (should be 1): ", 1); // Prompt for 1
        String itemCategory = getStringInput("Enter item category: ", true);

        try {
            // Ensure item quantity is 1 based on our discussion
            if (itemQuantity != 1) {
                 System.out.println("Warning: Item quantity is set to 1 based on program logic.");
                 itemQuantity = 1;
            }

            // Check if category exists
            Category existingCategory = inventoryManager.findCategoryByName(itemCategory);

            if (existingCategory == null) {
                // Category not found, prompt to create
                System.out.println("Category '" + itemCategory + "' not found.");
                boolean createCategory = confirmAction("Do you want to create this category?");

                if (createCategory) {
                    // Create the new category
                    inventoryManager.addCategory(itemCategory, 0); // Initial quantity is 0, will be updated by item
                    System.out.println("Category '" + itemCategory + "' created.");

                    // Proceed with adding the item
                    Item newItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                    inventoryManager.addItem(newItem);
                    inventoryManager.logTransaction("ADD", modelName, modelNumber, itemQuantity);
                    inventoryManager.saveData(); // Save all data
                    System.out.println("Item added successfully!");
                } else {
                    System.out.println("Item not added because the category was not created.");
                }
            } else {
                // Category exists, display categories and proceed with adding item
                System.out.println("Category '" + itemCategory + "' found. Existing categories:");
                inventoryManager.viewCategories(); // Display existing categories

                Item newItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                inventoryManager.addItem(newItem);
                inventoryManager.logTransaction("ADD", modelName, modelNumber, itemQuantity);
                inventoryManager.saveData(); // Save all data
                System.out.println("Item added successfully!");
            }

        } catch (Exception e) {
            System.err.println("Error adding item: " + e.getMessage());
        }
    }

    private static void removeItemConsole() {
        System.out.println("\n=== Remove Item ===");
        System.out.println("1. Remove by Model Number");
        System.out.println("2. Remove by Category");
        int choice = getIntInput("Enter your choice: ");

        try {
            switch (choice) {
                case 1:
                    String modelNumber = getStringInput("Enter model number to remove: ", true);
                    if (confirmAction("Are you sure you want to remove this item?")) {
                        // Log transaction before removing as we need item details
                        Item itemToRemove = inventoryManager.searchItem(modelNumber); // Assuming searchItem works by number
                        if (itemToRemove != null) {
                             inventoryManager.logTransaction("REMOVE", itemToRemove.getModelName(), itemToRemove.getModelNumber(), itemToRemove.getItemQuantity());
                        }
                        inventoryManager.removeItemByNumber(modelNumber);
                         // Saving data is handled by saveData() or on exit if implemented
                        // availabilityChecker.exportData(inventoryManager.getInventoryItems()); // Removed redundant save
                        System.out.println("Item removed successfully!");
                    }
                    break;
                case 2:
                    String category = getStringInput("Enter category to remove: ", true);
                    if (confirmAction("Are you sure you want to remove all items in this category?")) {
                         // Note: Removing by category in InventoryMgt will iterate and log for each item removed internally
                        inventoryManager.removeItemByCategory(category);
                         // Saving data is handled by saveData() or on exit if implemented
                        // availabilityChecker.exportData(inventoryManager.getInventoryItems()); // Removed redundant save
                        System.out.println("Items removed successfully!");
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.err.println("Error removing item(s): " + e.getMessage());
        }
    }

    private static void searchItemConsole() {
        System.out.println("\n=== Search Item ===");
        String searchTerm = getStringInput("Enter model number or name to search: ", true);
        
        try {
            Item foundItem = inventoryManager.searchItem(searchTerm);
            if (foundItem != null) {
                System.out.println("\nItem found:");
                System.out.println("Model Number: " + foundItem.getModelNumber());
                System.out.println("Model Name: " + foundItem.getModelName());
                System.out.println("Price: " + foundItem.getModelPrice());
                System.out.println("Quantity: " + foundItem.getItemQuantity());
                System.out.println("Category: " + foundItem.getItemCategory());
                availabilityChecker.checkAvailability(foundItem.getItemQuantity());
            } else {
                System.out.println("Item not found.");
            }
        } catch (Exception e) {
            System.err.println("Error searching for item: " + e.getMessage());
        }
    }

    private static void manageCategoriesConsole() {
        System.out.println("\n=== Manage Categories ===");
        System.out.println("1. Add Category");
        System.out.println("2. Remove Category");
        System.out.println("3. View Categories");
        int choice = getIntInput("Enter your choice: ");

        try {
            switch (choice) {
                case 1:
                    String categoryName = getStringInput("Enter category name: ", true);
                    int quantity = getIntInput("Enter initial quantity: ", 0);
                    inventoryManager.addCategory(categoryName, quantity);
                    System.out.println("Category added successfully!");
                    break;
                case 2:
                    categoryName = getStringInput("Enter category name to remove: ", true);
                    if (confirmAction("Are you sure you want to remove this category?")) {
                        inventoryManager.removeCategory(categoryName);
                        System.out.println("Category removed successfully!");
                    }
                    break;
                case 3:
                    inventoryManager.viewCategories();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.err.println("Error managing categories: " + e.getMessage());
        }
    }

    private static void viewInventoryConsole() {
        System.out.println("\n=== Current Inventory ===");
        // Get inventory items from InventoryMgt and display them
        List<Item> currentInventory = inventoryManager.getInventoryItems();
        if (currentInventory.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        for (Item item : currentInventory) {
            System.out.println("Item: " + item.getModelName() + " (" + item.getModelNumber() + ")");
            System.out.println("Price: " + item.getModelPrice() + ", Quantity: " + item.getItemQuantity()); // Will always be 1 based on logic
            System.out.println("Category: " + item.getItemCategory());
            // Use Availability to check/display status
            System.out.println("Status: " + (availabilityChecker.checkAvailability(item.getItemQuantity()) ? "Available" : "Out of Stock"));

            // Get category quantity and name for restock warning
            Category itemCategoryObject = inventoryManager.findCategoryByName(item.getItemCategory());
            int categoryQuantity = 0;
            String categoryName = item.getItemCategory();
            if (itemCategoryObject != null) {
                categoryQuantity = itemCategoryObject.getCategoryQuantity();
            }
            availabilityChecker.displayRestockWarning(item.getModelName(), item.getItemQuantity(), categoryQuantity, categoryName); // Corrected method call
            System.out.println("------------------------");
        }
    }

    private static void viewTransactionLogConsole() {
        System.out.println("\n--- Transaction Log ---");
        // Get transaction logs by reading from the file each time
        List<String> logs = inventoryManager.readTransactionLogsFromFile(); // Call the new method
        if (logs.isEmpty()) {
            System.out.println("Transaction log is empty.");
            return;
        }
        for (String logEntry : logs) {
            System.out.println(logEntry); // Log entries already contain the prefix if loaded from file
        }
        System.out.println("------------------------");
    }

    private static String getStringInput(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!required || !input.isEmpty()) {
                return input;
            }
            System.out.println("This field is required. Please try again.");
        }
    }

    private static int getIntInput(String prompt) {
        return getIntInput(prompt, Integer.MIN_VALUE);
    }

    private static int getIntInput(String prompt, int minValue) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= minValue) {
                    return value;
                }
                System.out.println("Please enter a number greater than or equal to " + minValue);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt, double minValue) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine());
                if (value >= minValue) {
                    return value;
                }
                System.out.println("Please enter a number greater than or equal to " + minValue);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static boolean confirmAction(String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                return true;
            } else if (response.equals("no")) {
                return false;
            }
            System.out.println("Please enter 'yes' or 'no'.");
        }
    }
}
