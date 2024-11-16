import java.util.*;
import java.io.*;
import java.util.Iterator; // Add this if not already present
import java.util.LinkedList; // Add this if not already present
import java.util.List; // Add this if not already present

// Remove these incorrect import statements
// import WareStage1.Wishlist; // Correct import statement
// import WareStage1.WishlistItem; // Correct import statement

public class Warehouse implements Serializable {
  private static final long serialVersionUID = 1L;
  private ClientList clientList;
  private Wishlist wishlist;
  private Catalog catalog;
  private Waitlist waitlist;
  private static Warehouse warehouse;

  private Warehouse() {
    clientList = ClientList.instance();
    wishlist = Wishlist.instance();
    catalog = Catalog.instance();
    waitlist = Waitlist.instance();
  }

  public static Warehouse instance() {
    if (warehouse == null) {
      return (warehouse = new Warehouse());
    } else {
      return warehouse;
    }
  }

  public Client addClient(String name, String address, String phone) {
    Client client = new Client(name, address, phone);
    if (clientList.insertClient(client)) {
      return client;
    }
    return null;
  }

  public Iterator<Client> getAllClients() {
    return clientList.getAllClients();
  }

  public Iterator<WishlistItem> getWishlistItems() {
    return wishlist.getItems();
  }

  public Product addProduct(String name, String id, int quantity, double price) {
    Product product = new Product(name, id, quantity, price);
    if (catalog.insertProduct(product)) {
      return product;
    }
    return null;
  }

  public Iterator<Product> getProducts() {
    return catalog.getProducts();
  }

  public Iterator<WaitlistItem> getWaitlist() {
    return waitlist.getItems();
  }

  public static Warehouse retrieve() {
    try {
      FileInputStream file = new FileInputStream("WarehouseData");
      ObjectInputStream input = new ObjectInputStream(file);
      input.readObject();
      return warehouse;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean save() {
    try {
      FileOutputStream file = new FileOutputStream("WarehouseData");
      ObjectOutputStream output = new ObjectOutputStream(file);
      output.writeObject(warehouse);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public Waitlist getWaitlistInstance() {
    return waitlist;
  }

  public void addItemToWishlist(String clientId, String productId, int quantity) {
    wishlist.addItem(clientId, productId, quantity);
  }

  public boolean removeItemFromWishlist(String clientId, String productId) {
    return wishlist.removeItem(clientId, productId);
  }

  public double processClientOrder(String clientId, Map<String, Integer> orderDetails) {
    Client client = clientList.search(clientId);
    if (client == null) {
        System.out.println("Client not found.");
        return 0.0;
    }

    double totalCost = 0.0;
    for (Map.Entry<String, Integer> entry : orderDetails.entrySet()) {
        String productId = entry.getKey();
        int requestedQuantity = entry.getValue();
        Product product = catalog.search(productId);
        if (product != null) {
            int availableQuantity = product.getQuantity();
            int fulfilledQuantity = Math.min(requestedQuantity, availableQuantity);
            if (fulfilledQuantity > 0) {
                product.reduceQuantity(fulfilledQuantity);
                wishlist.removeItem(clientId, productId);
                totalCost += product.getPrice() * fulfilledQuantity;
                // Record the transaction for the fulfilled quantity
                recordTransaction(clientId, productId, fulfilledQuantity, product.getPrice() * fulfilledQuantity, Transaction.Type.ORDER);
            }
            if (requestedQuantity > availableQuantity) {
                waitlist.addProduct(clientId, productId, requestedQuantity - availableQuantity);
                wishlist.removeItem(clientId, productId);
            }
        }
    }
    client.debit(totalCost);
    return totalCost;
  }

  public void receiveShipment(String productId, int quantityReceived) {
    Product product = catalog.search(productId);
    if (product == null) {
        System.out.println("Product not found.");
        return;
    }

    Iterator<WaitlistItem> waitlistItems = waitlist.getItems();
    while (waitlistItems.hasNext() && quantityReceived > 0) {
        WaitlistItem item = waitlistItems.next();
        if (item.getProductId().equals(productId)) {
            int quantityToFulfill = Math.min(item.getQuantity(), quantityReceived);
            quantityReceived -= quantityToFulfill;
            waitlistItems.remove();

            // Update client's balance and record transaction
            Client client = clientList.search(item.getClientId());
            if (client != null) {
                double cost = product.getPrice() * quantityToFulfill;
                client.debit(cost);
                client.addWaitlistFulfillmentTransaction(productId, quantityToFulfill, cost);
            }
        }
    }
    product.reduceQuantity(-quantityReceived); // Add remaining to stock
  }

  public void receivePayment(String clientId, double amount) {
    Client client = clientList.search(clientId);
    if (client == null) {
        System.out.println("Client not found.");
        return;
    }
    client.receivePayment(amount);
  }

  public Iterator<WishlistItem> getWishlistItemsForClient(String clientId) {
    List<WishlistItem> clientWishlist = new ArrayList<>();
    Iterator<WishlistItem> allItems = wishlist.getItems();
    while (allItems.hasNext()) {
        WishlistItem item = allItems.next();
        if (item.getClientId().equals(clientId)) {
            clientWishlist.add(item);
        }
    }
    if (clientWishlist.isEmpty()) {
        System.out.println("Wishlist is empty for client: " + clientId);
    }
    return clientWishlist.iterator();
  }

  public void addToWaitlist(String clientId, String productId, int quantity) {
    waitlist.addProduct(clientId, productId, quantity);
  }

  public Iterator<WaitlistItem> getWaitlistForClient(String clientId) {
    List<WaitlistItem> clientWaitlist = new ArrayList<>();
    Iterator<WaitlistItem> allItems = waitlist.getItems();
    while (allItems.hasNext()) {
        WaitlistItem item = allItems.next();
        if (item.getClientId().equals(clientId)) {
            clientWaitlist.add(item);
        }
    }
    if (clientWaitlist.isEmpty()) {
        System.out.println("Waitlist is empty for client: " + clientId);
    }
    return clientWaitlist.iterator();
  }

  public boolean removeProductFromCatalog(String productId) {
    return catalog.removeProduct(productId);
  }

  public Client getClient(String clientId) {
    return clientList.search(clientId);
  }

  public void recordTransaction(String clientId, String productId, int quantity, double totalCost, Transaction.Type type) {
    Client client = getClient(clientId);
    if (client != null) {
        switch (type) {
            case PAYMENT:
                client.receivePayment(totalCost);
                break;
            case ORDER:
                client.addOrderTransaction(productId, quantity, totalCost);
                break;
            case WAITLIST_FULFILLMENT:
                client.addWaitlistFulfillmentTransaction(productId, quantity, totalCost);
                break;
        }
    } else {
        System.out.println("Client not found for transaction recording.");
    }
  }

  public Iterator<WaitlistItem> getWaitlistForProduct(String productId) {
    List<WaitlistItem> productWaitlist = new ArrayList<>();
    Iterator<WaitlistItem> allItems = waitlist.getItems();
    while (allItems.hasNext()) {
        WaitlistItem item = allItems.next();
        if (item.getProductId().equals(productId)) {
            productWaitlist.add(item);
        }
    }
    return productWaitlist.iterator();
  }

  public void fulfillWaitlistItem(String clientId, String productId, int quantity) {
    Client client = clientList.search(clientId);
    Product product = catalog.search(productId);
    if (client != null && product != null) {
        double cost = product.getPrice() * quantity;
        client.debit(cost);
        client.addWaitlistFulfillmentTransaction(productId, quantity, cost);
    } else {
        System.out.println("Client or product not found for fulfilling waitlist item.");
    }
  }

  public Product getProduct(String productId) {
    return catalog.search(productId);
  }
}
