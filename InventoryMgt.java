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

public class InventoryMgt {
    private List<Item> inventoryItems;
    private List<Category> itemCategories;
    private Availability availabilityChecker;
    private static final String INVENTORY_FILE = "inventory.csv";
    private static final String SAVED_ITEMS_FILE = "saved_items.csv";
    private static final String LOGBOOK_FILE = "logbook.csv";
    private static final String CATEGORIES_FILE = "categories.csv";
    private static final String UNCATEGORIZED = "Uncategorized";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InventoryMgt() {
        inventoryItems = new ArrayList<>();
        itemCategories = new ArrayList<>();
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

    private Category findCategoryByName(String categoryName) {
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
        // Initialize with default categories
        addCategory("Engine Parts", 0);
        addCategory("Electrical", 0);
        addCategory("Body Parts", 0);
        addCategory("Accessories", 0);
    }

    public void importData() {
        System.out.println("Attempting to import data from " + INVENTORY_FILE + "...");
        inventoryItems.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        String modelNumber = parts[0].trim();
                        String modelName = parts[1].trim();
                        double modelPrice = Double.parseDouble(parts[2].trim());
                        int itemQuantity = Integer.parseInt(parts[3].trim());
                        String itemCategory = (parts.length >= 5) ? parts[4].trim() : UNCATEGORIZED;

                        Item importedItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                        inventoryItems.add(importedItem);
                        updateCategoryQuantity(itemCategory, itemQuantity);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line format in " + INVENTORY_FILE + ": " + line);
                    }
                }
            }
            System.out.println("Data imported successfully from " + INVENTORY_FILE + ". Total items: " + inventoryItems.size());
        } catch (IOException e) {
            System.out.println("No existing " + INVENTORY_FILE + " found or error reading file. Starting with empty inventory.");
        }
    }

    public void exportData() {
        System.out.println("Exporting data to " + INVENTORY_FILE + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            for (Item item : inventoryItems) {
                writer.println(item.getModelNumber() + "," + item.getModelName() + "," + 
                             item.getModelPrice() + "," + item.getItemQuantity() + "," + 
                             item.getItemCategory());
            }
            System.out.println("Data exported successfully to " + INVENTORY_FILE + ".");
        } catch (IOException e) {
            System.err.println("Error exporting data to file: " + e.getMessage());
        }
    }

    public void saveCategories() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CATEGORIES_FILE))) {
            for (Category category : itemCategories) {
                writer.println(category.getCategoryName() + "," + category.getCategoryQuantity());
            }
            System.out.println("Categories saved successfully to " + CATEGORIES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving categories: " + e.getMessage());
        }
    }

    private void logTransaction(String action, String modelName, String modelNumber, int quantity) {
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOGBOOK_FILE, true))) {
            String timestamp = dateFormat.format(new Date());
            logWriter.println(timestamp + "," + action + "," + modelName + "," + modelNumber + "," + quantity);
        } catch (IOException e) {
            System.err.println("Error writing to logbook file: " + e.getMessage());
        }
    }
}