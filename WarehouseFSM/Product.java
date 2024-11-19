import java.io.Serializable;

public class Product implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String id;
  private int quantity;
  private double price;

  public Product(String name, String id, int quantity, double price) {
    this.name = name;
    this.id = id;
    this.quantity = quantity;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getPrice() {
    return price;
  }

  public void reduceQuantity(int amount) {
    if (amount <= quantity) {
      quantity -= amount;
    }
  }

  public String toString() {
    return "ID: " + id + " | Name: " + name + " | Quantity: " + quantity + " | Price: $" + price;
  }
}