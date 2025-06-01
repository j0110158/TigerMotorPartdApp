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

public class InventoryMgt {
    private List<Item> inventoryItems;
    private List<Category> itemCategories;
    private List<String> transactionLogs;
    private Availability availabilityChecker;

    public static final String DATA_FILE = "inventory_data.csv";
    private static final String UNCATEGORIZED = "Uncategorized";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InventoryMgt() {
        inventoryItems = new ArrayList<>();
        itemCategories = new ArrayList<>();
        transactionLogs = new ArrayList<>();
        availabilityChecker = new Availability();
    }

    public List<Item> getInventoryItems() {
        return inventoryItems;
    }

    private Item findItemByModelNumber(String modelNumber) {
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
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (findItemByModelNumber(item.getModelNumber()) != null) {
            throw new IllegalStateException("Item with model number " + item.getModelNumber() + " already exists");
        }
        inventoryItems.add(item);
        updateCategoryQuantity(item.getItemCategory(), item.getItemQuantity());
    }

    public void removeItemByNumber(String modelNumber) {
        if (modelNumber == null || modelNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Model number cannot be null or empty");
        }
        Iterator<Item> iterator = inventoryItems.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getModelNumber().equals(modelNumber)) {
                updateCategoryQuantity(item.getItemCategory(), -item.getItemQuantity());
                iterator.remove();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalStateException("Item with model number " + modelNumber + " not found");
        }
    }

    public void removeItemByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        Iterator<Item> iterator = inventoryItems.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getItemCategory().equals(category)) {
                updateCategoryQuantity(item.getItemCategory(), -item.getItemQuantity());
                iterator.remove();
                found = true;
            }
        }
        if (!found) {
            throw new IllegalStateException("No items found in category " + category);
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
    }

    public void removeCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        boolean removed = itemCategories.removeIf(category -> category.getCategoryName().equals(categoryName));
        if (!removed) {
            throw new IllegalStateException("Category " + categoryName + " not found");
        }
    }

    public void updateCategoryQuantity(String categoryName, int quantityChange) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        Category category = findCategoryByName(categoryName);
        if (category == null) {
            throw new IllegalStateException("Category " + categoryName + " not found");
        }
        category.increaseQuantity(quantityChange);
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

    public void loadCategories() {
        // Categories are now loaded from the data file in importData()
        // Hardcoded initialization is removed.
    }

    public void importData(String filePath) {
        System.out.println("Attempting to import data from " + filePath + "...");
        inventoryItems.clear();
        itemCategories.clear(); // Clear existing categories
        transactionLogs.clear(); // Clear existing logs

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || (!line.startsWith("CATEGORY,") && !line.startsWith("ITEM,") && !line.startsWith("LOG,"))) {
                    // Skip empty lines or lines not starting with a known prefix
                    continue;
                }

                if (line.startsWith("CATEGORY,")) {
                    String[] parts = line.substring("CATEGORY,".length()).split(",");
                    if (parts.length >= 1) {
                        try {
                            String categoryName = parts[0].trim();
                            // We will recalculate category quantity based on items later
                            Category category = new Category(categoryName, 0); // Initialize with 0 quantity
                            itemCategories.add(category);
                        } catch (Exception e) {
                            System.err.println("Skipping invalid CATEGORY line in " + filePath + ": " + line);
                        }
                    }
                } else if (line.startsWith("ITEM,")) {
                    String[] parts = line.substring("ITEM,".length()).split(",");
                    if (parts.length >= 5) { // ModelNumber, ModelName, ModelPrice, ItemQuantity, ItemCategory
                        try {
                            String modelNumber = parts[0].trim();
                            String modelName = parts[1].trim();
                            double modelPrice = Double.parseDouble(parts[2].trim());
                            int itemQuantity = Integer.parseInt(parts[3].trim()); // Expecting 1 based on discussion
                            String itemCategory = parts[4].trim();

                            if (itemQuantity != 1) {
                                System.err.println("Warning: Item quantity in file is not 1. Using 1 for item " + modelNumber);
                                itemQuantity = 1; // Enforce program logic quantity
                            }

                            Item importedItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                            inventoryItems.add(importedItem);
                            // Category quantity will be updated after all items are loaded
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping invalid ITEM line (Number Format Error) in " + filePath + ": " + line);
                        } catch (Exception e) {
                             System.err.println("Skipping invalid ITEM line in " + filePath + ": " + line);
                        }
                    }
                } else if (line.startsWith("LOG,")) {
                     // LOG,Timestamp,Action,ModelName,ModelNumber,Quantity,Category (optional)
                    String logEntry = line.substring("LOG,".length());
                    transactionLogs.add(logEntry); // Add raw log entry for now
                }
            }

            // After loading, recalculate category quantities based on items
            for (Category category : itemCategories) {
                category.setCategoryQuantity(0); // Reset category quantity before summing
            }
            for (Item item : inventoryItems) {
                 Category category = findCategoryByName(item.getItemCategory());
                 if (category != null) {
                     category.increaseQuantity(item.getItemQuantity()); // Add item quantity (which is 1)
                 } else {
                     System.err.println("Warning: Item " + item.getModelNumber() + " has category " + item.getItemCategory() + " which was not found during import.");
                     // Optionally create a default category or handle this case
                 }
            }

            System.out.println("Data imported successfully from " + filePath + ". Total items: " + inventoryItems.size() + ", Categories: " + itemCategories.size() + ", Log entries: " + transactionLogs.size());
        } catch (IOException e) {
            System.out.println("No existing " + filePath + " found or error reading file. Starting with empty inventory and categories.");
        }
    }

    public void exportData(String filePath) {
        System.out.println("Exporting data to " + filePath + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write Categories
            writer.println("CATEGORY DATA");
            writer.println(); // Blank line for readability
            for (Category category : itemCategories) {
                writer.println("CATEGORY," + category.getCategoryName() + "," + category.getCategoryQuantity());
            }

            writer.println(); // Blank line for readability

            // Write Items
            writer.println("ITEM DATA");
            writer.println(); // Blank line for readability
            for (Item item : inventoryItems) {
                writer.println("ITEM," + item.getModelNumber() + "," + item.getModelName() + "," +
                               item.getModelPrice() + "," + item.getItemQuantity() + "," + // Item quantity should be 1
                               item.getItemCategory());
            }

            writer.println(); // Blank line for readability

            // Write Logs
            writer.println("LOG DATA");
             writer.println(); // Blank line for readability
            for (String logEntry : transactionLogs) {
                 writer.println("LOG," + logEntry); // Write the raw log entry back
            }

            System.out.println("Data exported successfully to " + filePath + ".");
        } catch (IOException e) {
            System.err.println("Error exporting data to file: " + e.getMessage());
        }
    }

    public void saveData() {
        System.out.println("Saving data to default file: " + DATA_FILE + "...");
        exportData(DATA_FILE); // Call exportData with the default file path
    }

    public void logTransaction(String action, String modelName, String modelNumber, int quantity) {
        String timestamp = dateFormat.format(new Date());
        // Assuming category is needed in log, might need to retrieve it based on item
        String itemCategory = "";
         Item item = findItemByModelNumber(modelNumber);
         if (item != null) {
             itemCategory = item.getItemCategory();
         }

        String logEntryContent = timestamp + "," + action + "," + modelName + "," + modelNumber + "," + quantity + "," + itemCategory;

        transactionLogs.add(logEntryContent); // Add to in-memory log list

        // Append to the data file immediately
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(DATA_FILE, true))) {
            logWriter.println("LOG," + logEntryContent);
        } catch (IOException e) {
            System.err.println("Error writing transaction to log file: " + e.getMessage());
        }
    }

    public List<String> getTransactionLogs() {
        return transactionLogs;
    }

    public List<String> readTransactionLogsFromFile() {
        List<String> logsFromFile = new ArrayList<>();
        System.out.println("Reading transaction log from file: " + DATA_FILE + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("LOG,")) {
                    logsFromFile.add(line.substring("LOG,".length()));
                }
            }
            System.out.println("Finished reading transaction log from file.");
        } catch (IOException e) {
            System.err.println("Error reading transaction log file: " + e.getMessage());
        }
        return logsFromFile;
    }

    public boolean dataFileExists() {
        File dataFile = new File(DATA_FILE);
        return dataFile.exists();
    }

    public boolean createDataFile() {
        System.out.println("Attempting to create data file: " + DATA_FILE + "...");
        try {
            File dataFile = new File(DATA_FILE);
            if (dataFile.createNewFile()) {
                System.out.println("Data file created successfully: " + DATA_FILE);
                return true;
            } else {
                System.out.println("Data file already exists: " + DATA_FILE);
                return true; // File already exists is also a success for creation check
            }
        } catch (IOException e) {
            System.err.println("Error creating data file: " + e.getMessage());
            return false;
        }
    }

    public List<Category> getItemCategories() {
        return itemCategories;
    }
}