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
    private static final String INVENTORY_FILE = "inventory.csv";
    private static final String LOGBOOK_FILE = "logbook.csv";
    private static final int MIN_QUANTITY_THRESHOLD = 5;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Item> inventoryItems = new ArrayList<>();

    /**
     * Checks if an item is available based on its quantity.
     * @param itemQuantity The quantity of the item to check
     * @return true if the item is available (quantity > 0), false otherwise
     */
    public boolean statusItem(int itemQuantity) {
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

    /**
     * Displays the transaction log from the specified file.
     * @param filename The name of the log file to read
     */
    public void logBook(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        System.out.println("\n--- Transaction Log ---");
        System.out.println("Displaying transaction log from " + filename + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading logbook file: " + e.getMessage(), e);
        }
    }

    /**
     * Displays the current inventory from the specified file.
     * @param filename The name of the inventory file to read
     */
    public void Inventory(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        System.out.println("\n--- Current Inventory ---");
        System.out.println("Displaying inventory data from " + filename + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String modelNumber = parts[0].trim();
                    String modelName = parts[1].trim();
                    String price = parts[2].trim();
                    String quantity = parts[3].trim();
                    String category = parts.length >= 5 ? parts[4].trim() : "Uncategorized";
                    
                    System.out.println("Item: " + modelName + " (" + modelNumber + ")");
                    System.out.println("Price: " + price + ", Quantity: " + quantity);
                    System.out.println("Category: " + category);
                    System.out.println("Status: " + (Integer.parseInt(quantity) > 0 ? "Available" : "Out of Stock"));
                    System.out.println("------------------------");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading inventory file: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing quantity in inventory file: " + e.getMessage(), e);
        }
    }

    /**
     * Imports inventory data from the inventory file.
     */
    public void importData() {
        System.out.println("Attempting to import data from " + INVENTORY_FILE + "...");
        inventoryItems.clear();

        try {
            // Create the file if it doesn't exist
            File file = new File(INVENTORY_FILE);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Created new " + INVENTORY_FILE + " file.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        try {
                            String modelNumber = parts[0].trim();
                            String modelName = parts[1].trim();
                            double modelPrice = Double.parseDouble(parts[2].trim());
                            int itemQuantity = Integer.parseInt(parts[3].trim());
                            String itemCategory = (parts.length >= 5) ? parts[4].trim() : "Uncategorized";

                            if (modelPrice < 0) {
                                throw new IllegalArgumentException("Model price cannot be negative");
                            }
                            if (itemQuantity < 0) {
                                throw new IllegalArgumentException("Item quantity cannot be negative");
                            }

                            Item importedItem = new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory);
                            inventoryItems.add(importedItem);
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping invalid line format in " + INVENTORY_FILE + ": " + line);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Skipping invalid item data in " + INVENTORY_FILE + ": " + line);
                        }
                    }
                }
                System.out.println("Data imported successfully from " + INVENTORY_FILE + ". Total items: " + inventoryItems.size());
            }
        } catch (IOException e) {
            System.out.println("No existing " + INVENTORY_FILE + " found or error reading file. Starting with empty inventory.");
        }
    }

    /**
     * Exports inventory data to the inventory file.
     * @param items The list of items to export
     */
    public void exportData(List<Item> items) {
        if (items == null) {
            items = new ArrayList<>(); // Create empty list if null
        }
        System.out.println("Exporting data to " + INVENTORY_FILE + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            for (Item item : items) {
                writer.println(item.getModelNumber() + "," + item.getModelName() + "," + 
                             item.getModelPrice() + "," + item.getItemQuantity() + "," + 
                             item.getItemCategory());
            }
            System.out.println("Data exported successfully to " + INVENTORY_FILE + ".");
        } catch (IOException e) {
            throw new RuntimeException("Error exporting data: " + e.getMessage(), e);
        }
    }

    /**
     * Logs a transaction to the logbook file.
     * @param action The action performed (e.g., "ADD", "REMOVE")
     * @param modelName The name of the model
     * @param modelNumber The model number
     * @param quantity The quantity involved in the transaction
     */
    public void logTransaction(String action, String modelName, String modelNumber, int quantity) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be null or empty");
        }
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        if (modelNumber == null || modelNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Model number cannot be null or empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOGBOOK_FILE, true))) {
            String timestamp = dateFormat.format(new Date());
            logWriter.println(timestamp + "," + action + "," + modelName + "," + modelNumber + "," + quantity);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to logbook file: " + e.getMessage(), e);
        }
    }
} 