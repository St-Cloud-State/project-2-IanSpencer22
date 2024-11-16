import java.io.Serializable;

public class WaitlistItem implements Serializable {
  private static final long serialVersionUID = 1L;
  private String clientId;
  private String productId;
  private int quantity;

  public WaitlistItem(String clientId, String productId, int quantity) {
    this.clientId = clientId;
    this.productId = productId;
    this.quantity = quantity;
  }

  public String getClientId() {
    return clientId;
  }

  public String getProductId() {
    return productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public String toString() {
    return "WaitlistItem[ClientId: " + clientId + " | ProductId: " + productId + " | Quantity: " + quantity + "]";
  }
}
