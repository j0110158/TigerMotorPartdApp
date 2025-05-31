import java.util.Scanner;

public class Console {
    private static Scanner scanner = new Scanner(System.in);
    private static InventoryMgt inventoryManager = new InventoryMgt();
    private static Availability availabilityChecker = new Availability();

    public static void start() {
        System.out.println("Welcome to Tiger Motor Parts Inventory Management System!");
        System.out.println("Initializing data...");
        
        try {
            // Initialize data
            availabilityChecker.importData();
            inventoryManager.loadCategories();
            
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
        int itemQuantity = getIntInput("Enter item quantity: ", 0);
        String itemCategory = getStringInput("Enter item category: ", true);

        try {
            Item newItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
            inventoryManager.addItem(newItem);
            availabilityChecker.logTransaction("ADD", modelName, modelNumber, itemQuantity);
            availabilityChecker.exportData(inventoryManager.getInventoryItems());
            System.out.println("Item added successfully!");
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
                        inventoryManager.removeItemByNumber(modelNumber);
                        availabilityChecker.exportData(inventoryManager.getInventoryItems());
                        System.out.println("Item removed successfully!");
                    }
                    break;
                case 2:
                    String category = getStringInput("Enter category to remove: ", true);
                    if (confirmAction("Are you sure you want to remove all items in this category?")) {
                        inventoryManager.removeItemByCategory(category);
                        availabilityChecker.exportData(inventoryManager.getInventoryItems());
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
                availabilityChecker.statusItem(foundItem.getItemQuantity());
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
        System.out.println("\n=== View Inventory ===");
        try {
            availabilityChecker.Inventory("inventory.csv");
        } catch (Exception e) {
            System.err.println("Error viewing inventory: " + e.getMessage());
        }
    }

    private static void viewTransactionLogConsole() {
        System.out.println("\n=== View Transaction Log ===");
        try {
            availabilityChecker.logBook("logbook.csv");
        } catch (Exception e) {
            System.err.println("Error viewing transaction log: " + e.getMessage());
        }
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
