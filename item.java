import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Item {
    // Attributes
    private double modelPrice;
    private String modelName;
    private String modelNumber;
    private int itemQuantity;
    private String itemCategory;

    // Constructor
    public Item(double modelPrice, String modelName, String modelNumber, int itemQuantity, String itemCategory) {
        this.modelPrice = modelPrice;
        this.modelName = modelName;
        this.modelNumber = modelNumber;
        this.itemQuantity = itemQuantity;
        this.itemCategory = itemCategory;
    }

    // Getters
    public double getModelPrice() {
        return modelPrice;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    // Setters
    public void setModelPrice(double price) {
        this.modelPrice = price;
    }

    public void setModelName(String name) {
        this.modelName = name;
    }

    public void setModelNumber(String number) {
        this.modelNumber = number;
    }

    public void setItemQuantity(int quantity) {
        this.itemQuantity = quantity;
    }

    public void setItemCategory(String category) {
        this.itemCategory = category;
    }

    // Method to increase quantity
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.itemQuantity += amount;
        }
    }

    // Method to decrease quantity
    public void decreaseQuantity(int amount) {
        if (amount > 0) {
            this.itemQuantity -= amount;
            if (this.itemQuantity < 0) {
                this.itemQuantity = 0;
            }
        }
    }

    // saveThisItem function
    public void saveThisItem() {
        String filename = "saved_items.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) { // Append to file
            // CSV format: modelNumber,modelName,modelPrice,itemQuantity,itemCategory
            writer.println(modelNumber + "," + modelName + "," + modelPrice + "," + itemQuantity + "," + itemCategory);
            System.out.println("Item details saved to " + filename + ": " + modelName);
        } catch (IOException e) {
            System.err.println("Error saving item details to file: " + e.getMessage());
            // TODO: Implement more robust error handling/logging.
        }
    }

    // TODO: Add methods for error handling as needed.
}
