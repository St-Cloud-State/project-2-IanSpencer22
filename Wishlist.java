import java.util.*;
import java.io.*;

public class Wishlist implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<WishlistItem> items = new LinkedList<>();
  private static Wishlist wishlist;

  private Wishlist() {}

  public static Wishlist instance() {
    if (wishlist == null) {
      return (wishlist = new Wishlist());
    } else {
      return wishlist;
    }
  }

  public void addItem(String clientId, String productId, int quantity) {
    items.add(new WishlistItem(clientId, productId, quantity));
  }

  public boolean removeItem(String clientId, String productId) {
    Iterator<WishlistItem> iterator = items.iterator();
    while (iterator.hasNext()) {
      WishlistItem item = iterator.next();
      if (item.getClientId().equals(clientId) && item.getProductId().equals(productId)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  public Iterator<WishlistItem> getItems() {
    return items.iterator();
  }

  private void writeObject(ObjectOutputStream output) throws IOException {
    output.defaultWriteObject();
    output.writeObject(wishlist);
  }

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    input.defaultReadObject();
    if (wishlist == null) {
      wishlist = (Wishlist) input.readObject();
    } else {
      input.readObject();
    }
  }

  public String toString() {
    return items.toString();
  }
}