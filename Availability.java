import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Availability {

    // statusItem(): BOOLEAN - Validates if an Item is available.
    // This method checks if the provided itemQuantity is greater than 0.
    // In a full application, this would likely take an Item object and check its quantity.
    public boolean statusItem(int itemQuantity) {
        boolean isAvailable = itemQuantity > 0;
        if (isAvailable) {
            System.out.println("Yes, this item is available.");
        } else {
            System.out.println("Sorry, this item is not available.");
        }
        return isAvailable;
    }

    // getDetails(ModelName, ModelPrice, itemCategory): toSTRING - Provides specific details of an available item.
    // This method currently just formats and returns the provided details as a String.
    // In a full application, it would likely take an Item object and format its attributes.
    public String getDetails(String modelName, double modelPrice, String itemCategory) {
        return "Details: Name=" + modelName + ", Price=" + modelPrice + ", Category=" + itemCategory;
    }

    // logBook() - Provides history of sold and added items.
    // This functionality requires storing transaction data over time.
    // This implementation reads and displays the content of the specified log file (logbook.csv).
    public void logBook(String filename) {
        System.out.println("\n--- Transaction Log ---");
        System.out.println("Displaying transaction log from " + filename + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading logbook file: " + e.getMessage());
            // TODO: Implement more robust error handling.
        }
    }

    // Inventory() - Allows manipulating raw inventory data in a csv-like template.
    // This implementation reads and displays the content of the specified inventory file (inventory.csv).
    // The actual manipulation/editing logic is handled in the Main class (manageInventoryConsole method)
    // by reading the data into memory, allowing edits, and then exporting the modified data back to the file.
    public void Inventory(String filename) {
        System.out.println("\n--- Raw Inventory Data (CSV Template) ---");
        System.out.println("Displaying raw inventory data from " + filename + "...");
         try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading inventory file: " + e.getMessage());
            // TODO: Implement more robust error handling.
        }
        // TODO: If direct text editing within the console was required, it would be implemented here,
        // but this is complex for a simple console application.
    }

    // TODO: Add methods for error handling specific to Availability checks or file operations.
} 