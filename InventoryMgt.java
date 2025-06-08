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
    private String dataFilePath; // This will now store the full file path
    private int lowStockThreshold = 5; // Default low stock threshold

    private static final String INVENTORY_DATA_FILENAME = "inventory_data.csv"; // New constant
    public static final String UNCATEGORIZED = "Uncategorized";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InventoryMgt() {
        inventoryItems = new ArrayList<>();
        itemCategories = new ArrayList<>();
        transactionLogs = new ArrayList<>();
        // Default file path: current working directory + filename
        this.dataFilePath = System.getProperty("user.dir") + File.separator + INVENTORY_DATA_FILENAME;
        this.availabilityChecker = new Availability(this.dataFilePath); // Initialize Availability with full path
        loadData(); // Load data from the default path
    }

    // New methods for low stock threshold
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative.");
        }
        this.lowStockThreshold = lowStockThreshold;
    }

    // New methods for data file path
    // This method now accepts a folderPath and constructs the full file path
    public void setDataFilePath(String folderPath) {
        if (folderPath == null || folderPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder path cannot be null or empty.");
        }
        // Construct the full file path from the selected folder
        this.dataFilePath = folderPath + File.separator + INVENTORY_DATA_FILENAME;
        this.availabilityChecker.setDataFilePath(this.dataFilePath); // Update Availability's full file path
    }

    // Getter for the current full data file path
    public String getDataFilePath() {
        return this.dataFilePath;
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
        if (quantityChange > 0) {
            category.increaseQuantity(quantityChange);
        } else if (quantityChange < 0) {
            category.decreaseQuantity(Math.abs(quantityChange)); // Use decreaseQuantity for negative changes
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

    public void loadCategories() {
        // Categories are now loaded from the data file in importData()
        // Hardcoded initialization is removed.
    }

    public void importData(String filePath) {
        List<String> rawData = availabilityChecker.readDataFromFile(filePath);
        // Process raw data to populate inventoryItems and itemCategories
        inventoryItems.clear();
        itemCategories.clear();
        transactionLogs.clear();

        for (String line : rawData) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("CATEGORY,")) {
                String[] parts = line.substring("CATEGORY,".length()).split(",");
                if (parts.length >= 1) {
                    try {
                        String categoryName = parts[0].trim();
                        Category category = new Category(categoryName, 0);
                        itemCategories.add(category);
                    } catch (Exception e) {
                        System.err.println("Skipping invalid CATEGORY line: " + line);
                    }
                }
            } else if (line.startsWith("ITEM,")) {
                String[] parts = line.substring("ITEM,".length()).split(",");
                if (parts.length >= 5) {
                    try {
                        String modelNumber = parts[0].trim();
                        String modelName = parts[1].trim();
                        double modelPrice = Double.parseDouble(parts[2].trim());
                        int itemQuantity = Integer.parseInt(parts[3].trim());
                        String itemCategory = parts[4].trim();

                        if (itemQuantity != 1) {
                            System.err.println("Warning: Item quantity in file is not 1. Using 1 for item " + modelNumber);
                            itemQuantity = 1;
                        }

                        Item importedItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                        inventoryItems.add(importedItem);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid ITEM line (Number Format Error): " + line);
                    } catch (Exception e) {
                        System.err.println("Skipping invalid ITEM line: " + line);
                    }
                }
            } else if (line.startsWith("LOG,")) {
                String logEntry = line.substring("LOG,".length());
                transactionLogs.add(logEntry);
            }
        }

        // Recalculate category quantities after loading all items
        for (Category category : itemCategories) {
            category.setCategoryQuantity(0);
        }
        for (Item item : inventoryItems) {
            Category category = findCategoryByName(item.getItemCategory());
            if (category != null) {
                category.increaseQuantity(item.getItemQuantity());
            } else {
                System.err.println("Warning: Item " + item.getModelNumber() + " has category " + item.getItemCategory() + " which was not found during import.");
            }
        }
        System.out.println("Data imported successfully from " + filePath + ". Total items: " + inventoryItems.size() + ", Categories: " + itemCategories.size() + ", Log entries: " + transactionLogs.size());
    }

    // Method to load data (internal, uses importData)
    public void loadData() {
        importData(this.dataFilePath);
    }
    
    // Modified exportData to use Availability
    public void exportData(String filePath) {
        StringBuilder dataToExport = new StringBuilder();
        // Export categories
        for (Category category : itemCategories) {
            dataToExport.append("CATEGORY,")
                        .append(category.getCategoryName())
                        .append("\n");
        }
        // Export items
        for (Item item : inventoryItems) {
            dataToExport.append("ITEM,")
                        .append(item.getModelNumber()).append(",")
                        .append(item.getModelName()).append(",")
                        .append(item.getModelPrice()).append(",")
                        .append(item.getItemQuantity()).append(",")
                        .append(item.getItemCategory())
                        .append("\n");
        }
        // Export logs
        for (String logEntry : transactionLogs) {
            dataToExport.append("LOG,").append(logEntry).append("\n");
        }
        availabilityChecker.writeDataToFile(filePath, dataToExport.toString());
    }

    // Modified saveData to use exportData
    public void saveData() {
        exportData(this.dataFilePath);
    }

    public void logTransaction(String action, String modelName, String modelNumber, int quantity) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("%s,%s,%s,%s,%d,%s",
                                        timestamp,
                                        action,
                                        modelName,
                                        modelNumber,
                                        quantity,
                                        findItemByModelNumber(modelNumber) != null ? findItemByModelNumber(modelNumber).getItemCategory() : "N/A"); // Include category if item exists
        transactionLogs.add(logEntry);
        // Save logs immediately after adding a transaction
        availabilityChecker.writeTransactionLogsToFile(transactionLogs);
    }

    public List<String> getTransactionLogs() {
        return transactionLogs;
    }

    // Modified to use Availability
    public List<String> readTransactionLogsFromFile() {
        return availabilityChecker.readTransactionLogsFromFile();
    }

    public List<Category> getItemCategories() {
        return itemCategories;
    }
}