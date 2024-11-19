import java.util.*;
import java.io.*;

public class Catalog implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Product> products = new LinkedList<>();
  private static Catalog catalog;

  private Catalog() {}

  public static Catalog instance() {
    if (catalog == null) {
      return (catalog = new Catalog());
    } else {
      return catalog;
    }
  }

  public boolean insertProduct(Product product) {
    products.add(product);
    return true;
  }

  public Product search(String productId) {
    for (Product product : products) {
      if (product.getId().equals(productId)) {
        return product;
      }
    }
    return null;
  }

  public Iterator<Product> getProducts() {
    return products.iterator();
  }

  public boolean removeProduct(String productId) {
    Iterator<Product> iterator = products.iterator();
    while (iterator.hasNext()) {
        Product product = iterator.next();
        if (product.getId().equals(productId)) {
            iterator.remove();
            return true;
        }
    }
    return false;
  }

  private void writeObject(ObjectOutputStream output) throws IOException {
    output.defaultWriteObject();
    output.writeObject(catalog);
  }

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    input.defaultReadObject();
    if (catalog == null) {
      catalog = (Catalog) input.readObject();
    } else {
      input.readObject();
    }
  }

  public String toString() {
    return products.toString();
  }
}
