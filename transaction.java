public class transaction {
    // Attributes
    private String transactionID;
    private String itemName;
    private int quantity;
    private double pricePerItem;

    // Constructor
    public transaction(String transactionID, String itemName, int quantity, double pricePerItem) {
        this.transactionID = transactionID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    // Getters
    public String getTransactionID() {
        return transactionID;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    // Setters
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }
    
}
