import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

public class Availability {
    private String dataFilePath;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Availability(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        // Ensure the data file exists when Availability is initialized
        createDataFile();
    }

    public void setDataFilePath(String dataFilePath) {
        if (dataFilePath == null || dataFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Data file path cannot be null or empty.");
        }
        this.dataFilePath = dataFilePath;
        createDataFile(); // Ensure the new data file exists
    }

    /**
     * Reads all lines from the data file.
     * @param filePath The path to the data file
     * @return A list of strings, where each string is a line from the file
     */
    public List<String> readDataFromFile(String filePath) {
        List<String> data = new ArrayList<>();
        System.out.println("Attempting to read data from " + filePath + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
            System.out.println("Data read successfully from " + filePath + ".");
        } catch (IOException e) {
            System.out.println("No existing " + filePath + " found or error reading file. Starting with empty data.");
        }
        return data;
    }

    /**
     * Writes the given data string to the specified file, overwriting existing content.
     * @param filePath The path to the file
     * @param data The string data to write
     */
    public void writeDataToFile(String filePath, String data) {
        System.out.println("Writing data to " + filePath + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(data);
            System.out.println("Data written successfully to " + filePath + ".");
        } catch (IOException e) {
            System.err.println("Error writing data to file: " + e.getMessage());
        }
    }

    /**
     * Appends transaction logs to the log file.
     * @param logs The list of transaction log entries
     */
    public void writeTransactionLogsToFile(List<String> logs) {
        // Overwrite the entire log file with the current logs to maintain consistency
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(this.dataFilePath))) {
            for (String logEntry : logs) {
                logWriter.println("LOG," + logEntry); // Ensure logs are written with the LOG prefix
            }
        } catch (IOException e) {
            System.err.println("Error writing transaction logs to file: " + e.getMessage());
        }
    }

    /**
     * Reads transaction logs from the data file.
     * @return A list of transaction log entries
     */
    public List<String> readTransactionLogsFromFile() {
        List<String> logsFromFile = new ArrayList<>();
        System.out.println("Reading transaction log from file: " + this.dataFilePath + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(this.dataFilePath))) {
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

    /**
     * Checks if the data file exists.
     * @return true if the data file exists, false otherwise
     */
    public boolean dataFileExists() {
        File file = new File(this.dataFilePath);
        return file.exists() && !file.isDirectory();
    }

    /**
     * Creates the data file if it does not exist.
     * @return true if the file was created or already exists, false on error
     */
    public boolean createDataFile() {
        System.out.println("Attempting to create data file: " + this.dataFilePath + "...");
        try {
            File file = new File(this.dataFilePath);
            if (file.createNewFile()) {
                System.out.println("Data file created successfully: " + this.dataFilePath);
                return true;
            } else {
                System.out.println("Data file already exists: " + this.dataFilePath);
                return true; // File already exists is also a success for creation check
            }
        } catch (IOException e) {
            System.err.println("Error creating data file: " + e.getMessage());
            return false;
        }
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