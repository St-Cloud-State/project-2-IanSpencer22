import java.util.*;
import java.io.*;

public class Waitlist implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<WaitlistItem> items = new LinkedList<>();
  private static Waitlist waitlist;

  private Waitlist() {}

  public static Waitlist instance() {
    if (waitlist == null) {
      return (waitlist = new Waitlist());
    } else {
      return waitlist;
    }
  }

  public void addProduct(String clientId, String productId, int quantity) {
    items.add(new WaitlistItem(clientId, productId, quantity));
  }

  public boolean removeProduct(String productId) {
    Iterator<WaitlistItem> iterator = items.iterator();
    while (iterator.hasNext()) {
      WaitlistItem item = iterator.next();
      if (item.getProductId().equals(productId)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  public Iterator<WaitlistItem> getItems() {
    return items.iterator();
  }

  private void writeObject(ObjectOutputStream output) throws IOException {
    output.defaultWriteObject();
    output.writeObject(waitlist);
  }

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    input.defaultReadObject();
    if (waitlist == null) {
      waitlist = (Waitlist) input.readObject();
    } else {
      input.readObject();
    }
  }

  public String toString() {
    return items.toString();
  }
}