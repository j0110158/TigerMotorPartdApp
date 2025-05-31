import java.util.ArrayList;
import java.util.List;

public class Category {
    // Attributes
    private String categoryName; // Changed to represent a single category name
    private int categoryQuantity; // Total quantity of items in this category

    // Constructor
    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.categoryQuantity = 0; // Start with zero quantity
    }

    // Getters
    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryQuantity() {
        return categoryQuantity;
    }

    // Setters
    // Removed setItemCategory as categoryName is a single string
    // Removed setCategoryQuantity(name:STRING) as quantity is managed directly

    // Method to set the total quantity for this category, with validation
    public void setCategoryQuantity(int quantity) {
        this.categoryQuantity = quantity;
        // Validate category quantity and trigger alert
        if (this.categoryQuantity < 5) {
            System.out.println("Good morning, please restock items for category: " + categoryName);
            // TODO: Implement a more robust alerting mechanism
        }
    }

    // Method to increase category quantity
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.categoryQuantity += amount;
             // The alert is triggered in setCategoryQuantity, so we call it here too after updating.
            setCategoryQuantity(this.categoryQuantity); // This will trigger the alert if needed
        }
    }

    // Method to decrease category quantity
    public void decreaseQuantity(int amount) {
         if (amount > 0) {
            this.categoryQuantity -= amount;
             if (this.categoryQuantity < 0) {
                this.categoryQuantity = 0; // Ensure quantity doesn't go below zero
            }
             // The alert is triggered in setCategoryQuantity, so we call it here too after updating.
            setCategoryQuantity(this.categoryQuantity); // This will trigger the alert if needed
        }
    }

    // Override equals and hashCode for easier searching in lists
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryName.equalsIgnoreCase(category.categoryName); // Compare category names (case-insensitive)
    }

    @Override
    public int hashCode() {
        return categoryName.toLowerCase().hashCode(); // Use lowercase for consistent hashing
    }

    // TODO: Add methods for error handling as needed.
}
