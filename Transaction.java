public class Transaction {
    public enum Type {
        ORDER, WAITLIST_FULFILLMENT, PAYMENT
    }

    private Type type;
    private String productId;
    private int quantity;
    private double totalCost;
    private double amount;

    public Transaction(Type type, String productId, int quantity, double totalCost) {
        this.type = type;
        this.productId = productId;
        this.quantity = quantity;
        this.totalCost = totalCost;
    }

    public Transaction(Type type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getAmount() {
        return amount;
    }
}
