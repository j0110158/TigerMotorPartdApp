import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.File;

public class Availability {
    private static final int MIN_QUANTITY_THRESHOLD = 5;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Availability() {
        // inventoryItems list initialization removed
    }

    /**
     * Checks if an item is available based on its quantity.
     * @param itemQuantity The quantity of the item to check
     * @return true if the item is available (quantity > 0), false otherwise
     */
    public boolean checkAvailability(int itemQuantity) {
        if (itemQuantity < 0) {
            throw new IllegalArgumentException("Item quantity cannot be negative");
        }
        boolean isAvailable = itemQuantity > 0;
        if (isAvailable) {
            System.out.println("Yes, this item is available.");
        } else {
            System.out.println("Sorry, this item is not available.");
        }
        return isAvailable;
    }

    /**
     * Checks if an item needs restock based on its quantity and the threshold.
     * @param itemQuantity The quantity of the item to check
     * @return true if the item needs restock, false otherwise
     */
    public boolean needsRestock(int itemQuantity) {
        if (itemQuantity < 0) {
            throw new IllegalArgumentException("Item quantity cannot be negative");
        }
        return itemQuantity <= MIN_QUANTITY_THRESHOLD && itemQuantity > 0;
    }

    /**
     * Displays a restock warning message for an item.
     * @param itemName The name of the item
     * @param itemQuantity The current quantity of the item
     */
    public void displayRestockWarning(String itemName, int itemQuantity, int categoryQuantity, String categoryName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (itemQuantity < 0) {
            throw new IllegalArgumentException("Item quantity cannot be negative");
        }
        if (needsRestock(categoryQuantity)) {
            System.out.println("\n!!! RESTOCK WARNING !!!");
            System.out.println("Category: " + categoryName + " is running low. Current Quantity: " + categoryQuantity);
            System.out.println("------------------------");
        }
    }

    /**
     * Gets formatted details of an item.
     * @param modelName The name of the model
     * @param modelPrice The price of the model
     * @param itemCategory The category of the item
     * @return A formatted string containing the item details
     */
    public String getDetails(String modelName, double modelPrice, String itemCategory) {
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        if (modelPrice < 0) {
            throw new IllegalArgumentException("Model price cannot be negative");
        }
        if (itemCategory == null || itemCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Item category cannot be null or empty");
        }
        return "Details: Name=" + modelName + ", Price=" + modelPrice + ", Category=" + itemCategory;
    }
} 