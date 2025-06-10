public class Category {
    private String categoryName;
    public int categoryQuantity;

    /**
     * Creates a new category with the specified name and zero quantity.
     * @param categoryName The name of the category
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public Category(String categoryName) {
        this(categoryName, 0);
    }

    /**
     * Creates a new category with the specified name and quantity.
     * @param categoryName The name of the category
     * @param categoryQuantity The initial quantity of the category
     * @throws IllegalArgumentException if categoryName is null or empty, or if categoryQuantity is negative
     */
    public Category(String categoryName, int categoryQuantity) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (categoryQuantity < 0) {
            throw new IllegalArgumentException("Category quantity cannot be negative");
        }
        this.categoryName = categoryName;
        this.categoryQuantity = categoryQuantity;
    }

    /**
     * Gets the name of the category.
     * @return The category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the name of the category.
     * @param categoryName The new category name
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.categoryName = categoryName;
    }

    /**
     * Gets the quantity of the category.
     * @return The category quantity
     */
    public int getCategoryQuantity() {
        return categoryQuantity;
    }

    /**
     * Sets the quantity of the category and checks if restocking is needed.
     * @param quantity The new category quantity
     * @throws IllegalArgumentException if quantity is negative
     */
    public void setCategoryQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Category quantity cannot be negative");
        }
        this.categoryQuantity = quantity;
    }

    /**
     * Increases the category quantity by the specified amount.
     * @param amount The amount to increase by
     * @throws IllegalArgumentException if amount is negative
     */
    public void increaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.categoryQuantity += amount;
    }

    /**
     * Decreases the category quantity by the specified amount.
     * @param amount The amount to decrease by
     * @throws IllegalArgumentException if amount is negative
     */
    public void decreaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.categoryQuantity = Math.max(0, this.categoryQuantity - amount);
    }

    /**
     * Checks if the category needs restocking based on the provided threshold.
     * @param lowStockThreshold The threshold to compare against
     * @return true if the category needs restocking, false otherwise
     */
    public boolean needsRestock(int lowStockThreshold) {
        return categoryQuantity < lowStockThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryName.equals(category.categoryName);
    }

    @Override
    public int hashCode() {
        return categoryName.hashCode();
    }
}
