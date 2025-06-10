import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Map; // Import Map

public class InventoryMgt {
    private List<Item> inventoryItems;
    private List<Category> itemCategories;
    private List<String> transactionLogs;
    private Availability availabilityChecker;
    private String dataFilePath; // This will now store the full file path
    private int lowStockThreshold; // Default low stock threshold will be read from file

    private static final String INVENTORY_DATA_FILENAME = "inventory_data.csv"; // New constant
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InventoryMgt() {
        inventoryItems = new ArrayList<>();
        itemCategories = new ArrayList<>();
        transactionLogs = new ArrayList<>();

        // Initialize Availability with the default path (current working directory)
        this.dataFilePath = System.getProperty("user.dir") + File.separator + INVENTORY_DATA_FILENAME;
        this.availabilityChecker = new Availability(this.dataFilePath); 

        // Load all data, including potentially stored dataFilePath and lowStockThreshold
        loadData(); 
    }

    // New methods for low stock threshold
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative.");
        }
        int oldThreshold = this.lowStockThreshold; // Capture old value for logging
        this.lowStockThreshold = lowStockThreshold;
        // Save all data, including the updated low stock threshold
        saveData();
        logTransaction("LOWSTOCK_SET", "", "", lowStockThreshold, "", oldThreshold); // Log the change
    }

    // New methods for data file path
    // This method now accepts a folderPath and constructs the full file path
    public void setDataFilePath(String folderPath) {
        try {
            if (folderPath == null || folderPath.trim().isEmpty()) {
                throw new IllegalArgumentException("Folder path cannot be null or empty.");
            }
            // Construct the full file path from the selected folder
            this.dataFilePath = folderPath + File.separator + INVENTORY_DATA_FILENAME;
            this.availabilityChecker.setDataFilePath(this.dataFilePath); // Update Availability's full file path
            saveData(); // Save all data with the new file path
        } catch (Exception e) {
            System.err.println("Error setting data file path: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Getter for the current full data file path
    public String getDataFilePath() {
        return this.dataFilePath;
    }

    public List<Item> getInventoryItems() {
        return inventoryItems;
    }

    public Item findItemByModelNumber(String modelNumber) {
        if (modelNumber == null || modelNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Model number cannot be null or empty");
        }
        for (Item item : inventoryItems) {
            if (item.getModelNumber().equalsIgnoreCase(modelNumber)) {
                return item;
            }
        }
        return null;
    }

    public Category findCategoryByName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        for (Category category : itemCategories) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public void addItem(Item item) {
        try {
            if (item == null) {
                throw new IllegalArgumentException("Item cannot be null");
            }
            if (findItemByModelNumber(item.getModelNumber()) != null) {
                throw new IllegalStateException("Item with model number " + item.getModelNumber() + " already exists");
            }
            inventoryItems.add(item);
            updateCategoryQuantity(item.getItemCategory(), item.getItemQuantity());
            saveData(); // Save data after adding an item
        } catch (Exception e) {
            System.err.println("Error adding item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeItemByNumber(String modelNumber) {
        try {
            if (modelNumber == null || modelNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Model number cannot be null or empty");
            }
            Iterator<Item> iterator = inventoryItems.iterator();
            boolean found = false;
            Item removedItem = null; // To store the removed item for logging
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (item.getModelNumber().equals(modelNumber)) {
                    updateCategoryQuantity(item.getItemCategory(), -item.getItemQuantity());
                    iterator.remove();
                    found = true;
                    removedItem = item;
                    removeCategoryIfEmpty(item.getItemCategory());
                    break;
                }
            }
            if (!found) {
                throw new IllegalStateException("Item with model number " + modelNumber + " not found");
            }
            saveData(); // Save data after removing an item
            if (removedItem != null) {
                logTransaction("REMOVE", removedItem.getModelName(), removedItem.getModelNumber(), removedItem.getItemQuantity(), removedItem.getItemCategory(), 0);
            }
        } catch (Exception e) {
            System.err.println("Error removing item by number: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeItemByCategory(String categoryName) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                throw new IllegalArgumentException("Category cannot be null or empty");
            }
            Iterator<Item> iterator = inventoryItems.iterator();
            boolean found = false;
            List<String> categoriesToCheck = new ArrayList<>(); // Collect categories to check after removal
            List<Item> removedItems = new ArrayList<>(); // To store removed items for logging
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (item.getItemCategory().equals(categoryName)) {
                    updateCategoryQuantity(item.getItemCategory(), -item.getItemQuantity());
                    categoriesToCheck.add(item.getItemCategory());
                    iterator.remove();
                    removedItems.add(item); // Add to removed items list
                    found = true;
                }
            }
            if (!found) {
                throw new IllegalStateException("No items found in category " + categoryName);
            }
            // Check categories for emptiness after all items are removed
            for (String catName : categoriesToCheck) {
                removeCategoryIfEmpty(catName);
            }
            saveData(); // Save data after removing items by category
            for (Item item : removedItems) {
                logTransaction("REMOVE_CATEGORY_ITEM", item.getModelName(), item.getModelNumber(), item.getItemQuantity(), item.getItemCategory(), 0);
            }
        } catch (Exception e) {
            System.err.println("Error removing item by category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to remove a category if its quantity becomes 0
    private void removeCategoryIfEmpty(String categoryName) {
        try {
            Category category = findCategoryByName(categoryName);
            if (category != null && category.getCategoryQuantity() == 0) {
                itemCategories.remove(category);
                System.out.println("Category '" + categoryName + "' removed as it is now empty.");
                logTransaction("CATEGORY_REMOVED", categoryName, "", 0, "", 0); // Log category removal
                saveData(); // Save data after category removal
            }
        } catch (Exception e) {
            System.err.println("Error removing category if empty: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Item searchItem(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        for (Item item : inventoryItems) {
            if (item.getModelNumber().equals(searchTerm) || 
                item.getModelName().equalsIgnoreCase(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    public void addCategory(String categoryName, int initialQuantity) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be null or empty");
            }
            if (initialQuantity < 0) {
                throw new IllegalArgumentException("Initial quantity cannot be negative");
            }
            if (findCategoryByName(categoryName) != null) {
                throw new IllegalStateException("Category " + categoryName + " already exists");
            }
            Category category = new Category(categoryName, initialQuantity);
            itemCategories.add(category);
            saveData(); // Save data after adding a category
        } catch (Exception e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeCategory(String categoryName) {
        try {
            boolean removed = itemCategories.removeIf(category -> category.getCategoryName().equals(categoryName));
            if (!removed) {
                throw new IllegalStateException("Category " + categoryName + " not found");
            }
            saveData(); // Save data after removing a category
        } catch (Exception e) {
            System.err.println("Error removing category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCategoryQuantity(String categoryName, int quantityChange) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be null or empty");
            }
            Category category = findCategoryByName(categoryName);
            if (category == null) {
                // If category doesn't exist, create it (especially important for adding items to new categories)
                addCategory(categoryName, 0); // Add with 0 quantity initially
                category = findCategoryByName(categoryName); // Retrieve the newly created category
            }

            if (quantityChange > 0) {
                category.increaseQuantity(quantityChange);
            } else if (quantityChange < 0) {
                category.decreaseQuantity(Math.abs(quantityChange)); // Use decreaseQuantity for negative changes
            }
            saveData(); // Save data after updating category quantity
        } catch (Exception e) {
            System.err.println("Error updating category quantity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewCategories() {
        if (itemCategories.isEmpty()) {
            System.out.println("\nNo categories defined.");
            return;
        }
        System.out.println("\n=== Categories ===");
        for (Category category : itemCategories) {
            System.out.println("Category: " + category.getCategoryName());
            System.out.println("Quantity: " + category.getCategoryQuantity());
            System.out.println("------------------------");
        }
    }

    public void loadData() {
        try {
            Map<String, Object> loadedData = availabilityChecker.readAllDataFromFile();
            this.inventoryItems = (List<Item>) loadedData.get("items");
            this.itemCategories = (List<Category>) loadedData.get("categories");
            this.transactionLogs = (List<String>) loadedData.get("logs");
            this.lowStockThreshold = (int) loadedData.get("lowStockThreshold");
            // Update the dataFilePath in InventoryMgt if it was loaded from the file
            this.dataFilePath = (String) loadedData.get("dataPath");
            this.availabilityChecker.setDataFilePath(this.dataFilePath); // Ensure availabilityChecker also has the updated path
            System.out.println("Data loaded successfully from " + dataFilePath);
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
            // Initialize with empty data if loading fails
            this.inventoryItems = new ArrayList<>();
            this.itemCategories = new ArrayList<>();
            this.transactionLogs = new ArrayList<>();
            this.lowStockThreshold = 5; // Default low stock threshold
            this.dataFilePath = System.getProperty("user.dir") + File.separator + INVENTORY_DATA_FILENAME;
            this.availabilityChecker.setDataFilePath(this.dataFilePath);
        }
    }

    public void exportData() {
        availabilityChecker.writeAllDataToFile(inventoryItems, itemCategories, lowStockThreshold, transactionLogs, dataFilePath);
    }

    public void saveData() {
        try {
            availabilityChecker.writeAllDataToFile(inventoryItems, itemCategories, lowStockThreshold, transactionLogs, dataFilePath);
            System.out.println("Data saved successfully to " + dataFilePath);
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void logTransaction(String action, String modelName, String modelNumber, int quantity, String categoryAffected, int oldValue) {
        String timestamp = dateFormat.format(new Date());
        String logEntry;

        if (action.equals("LOWSTOCK_SET")) {
            logEntry = "LOWSTOCK," + timestamp + ",SET," + quantity + "," + oldValue + ",";
        } else {
            logEntry = "LOG," + timestamp + "," + action + "," + modelName + "," + modelNumber + "," + quantity + "," + categoryAffected;
        }
        transactionLogs.add(logEntry);
        // Logs are now written as part of the comprehensive saveData() call
    }

    public List<String> getTransactionLogs() {
        return transactionLogs;
    }

    public List<Category> getItemCategories() {
        return itemCategories;
    }

    public List<Category> getLowStockCategories() {
        List<Category> lowStock = new ArrayList<>();
        for (Category category : itemCategories) {
            if (category.getCategoryQuantity() < lowStockThreshold) {
                lowStock.add(category);
            }
        }
        return lowStock;
    }

    public double calculateTotalInventoryWorth() {
        double totalWorth = 0.0;
        for (Item item : inventoryItems) {
            totalWorth += item.getModelPrice() * item.getItemQuantity();
        }
        return totalWorth;
    }
}