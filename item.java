package TigerMotorPartdApp;

import java.util.Objects;

/**
 * Represents an item with a model price, name, and number.
 * This class provides getters and setters for its attributes,
 * along with standard methods like toString, equals, and hashCode.
 */
public class item {
    // Attributes
    private double modelPrice;
    private String modelName;
    private String modelNumber;

    /**
     * Default constructor.
     * Initializes modelPrice to 0.0 and modelName/modelNumber to null.
     */
    public item() {
    }

    /**
     * Parameterized constructor to initialize an item.
     *
     * @param modelPrice  The price of the model. Must be non-negative.
     * @param modelName   The name of the model. Must not be null or empty.
     * @param modelNumber The number of the model. Must not be null or empty.
     * @throws IllegalArgumentException if any of the validation rules are violated.
     */
    public item(double modelPrice, String modelName, String modelNumber) {
        if (modelPrice < 0) {
            throw new IllegalArgumentException("Model price cannot be negative.");
        }
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty.");
        }
        if (modelNumber == null || modelNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Model number cannot be null or empty.");
        }
        this.modelPrice = modelPrice;
        this.modelName = modelName;
        this.modelNumber = modelNumber;
    }

    /**
     * Gets the model price as a String.
     * Note: The original design specified returning a String.
     * Consider returning double if numerical operations are common.
     *
     * @return The model price formatted as a String.
     */
    public String getModelPriceString() { // Renamed for clarity
        return String.valueOf(modelPrice);
    }

    /**
     * Gets the model price as a double.
     *
     * @return The model price.
     */
    public double getModelPrice() {
        return modelPrice;
    }

    /**
     * Sets the model price.
     *
     * @param price The new price for the model. Must be non-negative.
     * @throws IllegalArgumentException if the price is negative.
     */
    public void setModelPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Model price cannot be negative.");
        }
        this.modelPrice = price;
    }

    /**
     * Gets the model name.
     *
     * @return The model name.
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name.
     *
     * @param name The new name for the model. Must not be null or empty.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public void setModelName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty.");
        }
        this.modelName = name;
    }

    /**
     * Gets the model number.
     *
     * @return The model number.
     */
    public String getModelNumber() {
        return modelNumber;
    }

    /**
     * Sets the model number.
     *
     * @param number The new number for the model. Must not be null or empty.
     * @throws IllegalArgumentException if the model number is null or empty.
     */
    public void setModelNumber(String number) { // Parameter name changed from 'name' to 'number'
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Model number cannot be null or empty.");
        }
        this.modelNumber = number;
    }

    /**
     * Returns a string representation of the item object.
     *
     * @return A string containing the model name, number, and price.
     */
    @Override
    public String toString() {
        return "item{" +
               "modelName='" + modelName + '\'' +
               ", modelNumber='" + modelNumber + '\'' +
               ", modelPrice=" + modelPrice +
               '}';
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return true if this object is the same as the o argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        item item = (item) o;
        return Double.compare(item.modelPrice, modelPrice) == 0 &&
               Objects.equals(modelName, item.modelName) &&
               Objects.equals(modelNumber, item.modelNumber);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(modelPrice, modelName, modelNumber);
    }
}