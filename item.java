

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
}
