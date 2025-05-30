package TigerMotorPartdApp;

public class item {
    // Attributes
    private double modelPrice;
    private String modelName;
    private String modelNumber;

    // Getter for modelPrice (returns String as per diagram)
    public String getModelPrice() {
        return String.valueOf(modelPrice);
    }

    // Setter for modelPrice
    public void setModelPrice(double price) {
        this.modelPrice = price;
    }

    // Getter for modelName
    public String getModelName() {
        return modelName;
    }

    // Setter for modelName
    public void setModelName(String name) {
        this.modelName = name;
    }

    // Getter for modelNumber
    public String getModelNumber() {
        return modelNumber;
    }

    // Setter for modelNumber
    public void setModelNumber(String name) {
        this.modelNumber = name;
    }
}