import java.util.*;
import java.text.*;
import java.io.*;

public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int HELP = 17;

  // Client-related commands
  private static final int ADD_CLIENT = 1;
  private static final int DISPLAY_ALL_CLIENTS = 2;

  // Product-related commands
  private static final int ADD_PRODUCT_TO_CATALOG = 3;
  private static final int REMOVE_PRODUCT_FROM_CATALOG = 4;
  private static final int SHOW_PRODUCTS = 5;

  // Wish/Waitlist-related commands
  private static final int ADD_PRODUCT_TO_WISHLIST = 6;
  private static final int REMOVE_PRODUCT_FROM_WISHLIST = 7;
  private static final int DISPLAY_PRODUCTS_IN_WISHLIST = 8;
  private static final int SHOW_WAITLIST = 9;
  private static final int REMOVE_WAITLIST_ITEM = 10;

  // Order-related commands
  private static final int PLACE_AN_ORDER = 11;
  private static final int PRINT_INVOICE = 12;
  private static final int RECEIVE_PAYMENT = 13;
  private static final int RECEIVE_SHIPMENT = 14; // Add this line

  // Data-related commands
  private static final int SAVE = 15;
  private static final int RETRIEVE = 16;

  private UserInterface() {
    if (yesOrNo("Look for saved data and use it?")) {
      retrieve();
    } else {
      warehouse = Warehouse.instance();
    }
  }

  public static UserInterface instance() {
    if (userInterface == null) {
      return userInterface = new UserInterface();
    } else {
      return userInterface;
    }
  }

  public String getToken(String prompt) {
    do {
      try {
        System.out.println(prompt);
        String line = reader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
        if (tokenizer.hasMoreTokens()) {
          return tokenizer.nextToken();
        }
      } catch (IOException ioe) {
        System.exit(0);
      }
    } while (true);
  }

  private boolean yesOrNo(String prompt) {
    String more = getToken(prompt + " (Y|y)[es] or anything else for no");
    return more.charAt(0) == 'y' || more.charAt(0) == 'Y';
  }

  public int getNumber(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        Integer num = Integer.valueOf(item);
        return num;
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a number ");
      }
    } while (true);
  }

  public int getCommand() {
    do {
      try {
        int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
        if (value >= EXIT && value <= HELP) {
          return value;
        }
      } catch (NumberFormatException nfe) {
        System.out.println("Enter a number");
      }
    } while (true);
  }

  public void help() {
    System.out.println("Enter a number between 0 and " + RECEIVE_PAYMENT + " as explained below:");
    System.out.println(EXIT + " to Exit");
    System.out.println(HELP + " for help\n");

    // Client-related commands
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(DISPLAY_ALL_CLIENTS + " to display all clients");
    // System.out.println(SHOW_CLIENT_BY_ID + " to show client by ID");

    // Product-related commands
    System.out.println(ADD_PRODUCT_TO_CATALOG + " to add a product to catalog");
    System.out.println(REMOVE_PRODUCT_FROM_CATALOG + " to remove a product from catalog");
    System.out.println(SHOW_PRODUCTS + " to show products");

    // Wishlist-related commands
    System.out.println(ADD_PRODUCT_TO_WISHLIST + " to add a product to wishlist");
    System.out.println(REMOVE_PRODUCT_FROM_WISHLIST + " to remove a product from wishlist");
    System.out.println(DISPLAY_PRODUCTS_IN_WISHLIST + " to display products in wishlist");
    System.out.println(SHOW_WAITLIST + " to show waitlist");
    System.out.println(REMOVE_WAITLIST_ITEM + " to remove an item from the waitlist");

    // Order-related commands
    System.out.println(PLACE_AN_ORDER + " to process a client's order");
    System.out.println(PRINT_INVOICE + " to print invoice");
    System.out.println(RECEIVE_PAYMENT + " to receive payment from a client");
    System.out.println(RECEIVE_SHIPMENT + " to receive a shipment");

    // Data-related commands
    System.out.println(SAVE + " to save data");
    System.out.println(RETRIEVE + " to retrieve data");
  }

  public void addClient() {
    System.out.println("Adding a new client...");
    String name = getToken("Enter client name");
    String address = getToken("Enter client address");
    String phone = getToken("Enter client phone");
    if (warehouse.addClient(name, address, phone) != null) {
      System.out.println("Client added successfully");
    } else {
      System.out.println("Could not add client");
    }
    System.out.println("");
  }

  public void showClients() {
    System.out.println("Displaying all clients...");
    Iterator<Client> clients = warehouse.getAllClients();
    while (clients.hasNext()) {
        Client client = clients.next();
        System.out.println("Id: " + client.getId() + 
                           " | Name: " + client.getName() + 
                           " | Address: " + client.getAddress() + 
                           " | Phone: " + client.getPhone() + 
                           " | Balance: $" + client.getBalance());
    }
    System.out.println("");
  }

  public void showProductOnWishList() {
    System.out.println("Displaying products on wishlist...");
    String clientId = getToken("Enter client id");

    Iterator<WishlistItem> wishListItems = warehouse.getWishlistItemsForClient(clientId);
    while (wishListItems.hasNext()) {
        WishlistItem wishItem = wishListItems.next();
        System.out.println(wishItem);
    }
    System.out.println("");
  }

  private void save() {
    System.out.println("Saving the warehouse...");
    if (warehouse.save()) {
      System.out.println("The warehouse has been successfully saved in the file WarehouseData");
    } else {
      System.out.println("There has been an error in saving");
    }
    System.out.println("");
  }

  private void retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println("The Warehouse has been successfully retrieved from the file WarehouseData");
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesn't exist; creating new warehouse");
        warehouse = Warehouse.instance();
      }
    } catch (Exception cnfe) {
      cnfe.printStackTrace();
    }
    System.out.println("");
  }

  public void addProduct() {
    System.out.println("Adding a new product...");
    String name = getToken("Enter product name");
    String id = getToken("Enter product id");
    int quantity = getNumber("Enter quantity");
    double price = getNumber("Enter price");
    Product product = warehouse.addProduct(name, id, quantity, price);
    if (product == null) {
      System.out.println("Could not add product");
    } else {
      System.out.println("Product added successfully");
    }
    System.out.println("");
  }

  public void showProducts() {
    System.out.println("Displaying all products...");
    Iterator<Product> products = warehouse.getProducts();
    while (products.hasNext()) {
      System.out.println(products.next());
    }
    System.out.println("");
  }

  public void showWaitlist() {
    System.out.println("Displaying waitlist for a product...");
    String productId = getToken("Enter product id");
    
    Iterator<WaitlistItem> items = warehouse.getWaitlistForProduct(productId);
    if (!items.hasNext()) {
        System.out.println("No waitlist items for product ID: " + productId);
    }
    while (items.hasNext()) {
        WaitlistItem item = items.next();
        System.out.println(item);
    }
    System.out.println("");
  }

  public void removeWaitlistItem() {
    System.out.println("Removing an item from the waitlist...");
    String id = getToken("Enter product id to remove from waitlist");
    if (warehouse.getWaitlistInstance().removeProduct(id)) {
      System.out.println("Product removed from waitlist");
    } else {
      System.out.println("Product not found in waitlist");
    }
    System.out.println("");
  }

  public void addItemToWishlist() {
    System.out.println("Adding an item to the wishlist...");
    String clientId = getToken("Enter client id");
    String productId = getToken("Enter product id");
    int quantity = getNumber("Enter quantity");
    warehouse.addItemToWishlist(clientId, productId, quantity);
    System.out.println("Item added to wishlist");
    System.out.println("");
  }

  public void removeItemFromWishlist() {
    System.out.println("Removing an item from the wishlist...");
    String clientId = getToken("Enter client id");
    String productId = getToken("Enter product id");
    if (warehouse.removeItemFromWishlist(clientId, productId)) {
      System.out.println("Item removed from wishlist");
    } else {
      System.out.println("Item not found in wishlist");
    }
    System.out.println("");
  }

  public void showWishlist() {
    System.out.println("Displaying items in the client's wishlist...");
    String clientId = getToken("Enter client id");

    Iterator<WishlistItem> items = warehouse.getWishlistItems();
    while (items.hasNext()) {
        WishlistItem item = items.next();
        if (item.getClientId().equals(clientId)) {
            System.out.println(item);
        }
    }
    System.out.println("");
  }

  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case ADD_CLIENT:
          addClient();
          break;
        case ADD_PRODUCT_TO_WISHLIST:
          addItemToWishlist();
          break;
        case DISPLAY_ALL_CLIENTS:
          showClients();
          break;
        case DISPLAY_PRODUCTS_IN_WISHLIST:
          showProductOnWishList();
          break;
        case ADD_PRODUCT_TO_CATALOG:
          addProduct();
          break;
        case REMOVE_PRODUCT_FROM_CATALOG:
          removeProductFromCatalog();
          break;
        case REMOVE_PRODUCT_FROM_WISHLIST:
          removeItemFromWishlist();
          break;
        case PLACE_AN_ORDER:
          processClientOrder();
          break;
        case PRINT_INVOICE:
          generateInvoice();
          break;
        case RECEIVE_PAYMENT:
          receivePayment();
          break;
        case SHOW_PRODUCTS:
          showProducts();
          break;
        case SAVE:
          save();
          break;
        case RETRIEVE:
          retrieve();
          break;
        case HELP:
          help();
          break;
        case SHOW_WAITLIST:
          showWaitlist();
          break;
        case REMOVE_WAITLIST_ITEM:
          removeWaitlistItem();
          break;
        case RECEIVE_SHIPMENT:
          receiveShipment();
          break;
      }
    }
  }

  public void processClientOrder() {
    System.out.println("Processing client order...");
    String clientId = getToken("Enter client id");
    Map<String, Integer> orderDetails = new HashMap<>();
    double totalCost = 0.0;

    Iterator<WishlistItem> wishlistItems = warehouse.getWishlistItems();
    while (wishlistItems.hasNext()) {
        WishlistItem item = wishlistItems.next();
        if (item.getClientId().equals(clientId)) {
            Product product = warehouse.getProduct(item.getProductId());
            if (product != null) {
                System.out.println("Product ID: " + item.getProductId() + 
                                   " | Quantity: " + item.getQuantity() + 
                                   " | Price: $" + product.getPrice());
            }
            String action = getToken("Enter action (order/drop/leave): ");
            if (action.equalsIgnoreCase("order")) {
                int quantity = getNumber("Enter quantity");
                orderDetails.put(item.getProductId(), quantity);
            } else if (action.equalsIgnoreCase("drop")) {
                warehouse.removeItemFromWishlist(clientId, item.getProductId());
            }
            // If "leave", do nothing and continue to the next item
        }
    }

    totalCost = warehouse.processClientOrder(clientId, orderDetails);
    System.out.println("Order Placed for " + clientId + ". Total cost: $" + totalCost);
    System.out.println("");
  }

  public void receiveShipment() {
    System.out.println("Receiving a shipment...");
    String productId = getToken("Enter product id");
    int quantity = getNumber("Enter quantity received");
    warehouse.receiveShipment(productId, quantity);

    // Check and fulfill waitlist items
    Iterator<WaitlistItem> waitlistItems = warehouse.getWaitlistForProduct(productId);
    while (waitlistItems.hasNext() && quantity > 0) {
      WaitlistItem item = waitlistItems.next();
      int fulfillQuantity = Math.min(quantity, item.getQuantity());
      warehouse.fulfillWaitlistItem(item.getClientId(), productId, fulfillQuantity);
      quantity -= fulfillQuantity;

      // Record the transaction
      warehouse.recordTransaction(item.getClientId(), productId, fulfillQuantity, 0, Transaction.Type.WAITLIST_FULFILLMENT);
    }
    System.out.println("Shipment received for " + productId);
    System.out.println("");
  }

  public void receivePayment() {
    System.out.println("Receiving a payment...");
    String clientId = getToken("Enter client id");
    double amount = Double.parseDouble(getToken("Enter payment amount"));
    warehouse.receivePayment(clientId, amount);
    System.out.println("Payment of $" + amount + " received from client " + clientId);
    System.out.println("");
  }

  public void generateInvoice() {
    System.out.println("Generating an invoice...");
    String clientId = getToken("Enter client id");
    Client client = warehouse.getClient(clientId);
    if (client == null) {
        System.out.println("Client not found.");
        return;
    }

    double totalCost = 0.0;
    double totalPayments = 0.0;

    System.out.println("------------------------------------");
    System.out.println("Invoice for Client ID: " + clientId);
    System.out.println("Name: " + client.getName());
    System.out.println("Address: " + client.getAddress());
    System.out.println("Phone: " + client.getPhone());

    // Processed Orders
    System.out.println("\nProcessed Orders:");
    List<Transaction> transactions = client.getTransactions();
    for (Transaction transaction : transactions) {
        if (transaction.getType() == Transaction.Type.ORDER && transaction.getProductId() != null) {
            System.out.println("Product ID: " + transaction.getProductId() +
                               ", Quantity: " + transaction.getQuantity() +
                               ", Total Cost: $" + transaction.getTotalCost());
            totalCost += transaction.getTotalCost();
        }
    }

    // Fulfilled Waitlist Items
    System.out.println("\nFulfilled Waitlist Items:");
    for (Transaction transaction : transactions) {
        if (transaction.getType() == Transaction.Type.WAITLIST_FULFILLMENT && transaction.getProductId() != null) {
            System.out.println("Product ID: " + transaction.getProductId() +
                               ", Quantity: " + transaction.getQuantity() +
                               ", Total Cost: $" + transaction.getTotalCost());
            totalCost += transaction.getTotalCost();
        }
    }

    // Payments Received
    System.out.println("\nPayments Received:");
    for (Transaction transaction : transactions) {
        if (transaction.getType() == Transaction.Type.PAYMENT) {
            System.out.println("Payment Amount: $" + transaction.getAmount());
            totalPayments += transaction.getAmount();
        }
    }

    // Summary
    System.out.println("\nSummary:");
    System.out.println("Total Cost of Orders and Fulfilled Waitlist Items: $" + totalCost);
    System.out.println("Total Payments Received: $" + totalPayments);
    System.out.println("Account Balance: $" + (totalPayments - totalCost));
    System.out.println("------------------------------------");
    System.out.println("");
  }

  public static void main(String[] s) {
    UserInterface.instance().process();
  }

  public void removeProductFromCatalog() {
    String productId = getToken("Enter product id to remove from catalog");
    if (warehouse.removeProductFromCatalog(productId)) {
        System.out.println("Product removed from catalog");
    } else {
        System.out.println("Product not found in catalog");
    }
    System.out.println("");
  }
}
