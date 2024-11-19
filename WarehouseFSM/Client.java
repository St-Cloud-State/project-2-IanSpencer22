import java.util.*;
import java.io.*;
public class Client implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String address;
  private String phone;
  private String id;
  private static final String CLIENT_STRING = "C";
  private static int idNum = 1;
  private List<WishlistItem> productsOnWishList = new LinkedList<>();
  private List<WaitlistItem> productsOnWaitList = new LinkedList<>();
  private List<Transaction> transactions = new LinkedList<>();
  private double balance;
  public  Client (String name, String address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.balance = 0.0;
    id = CLIENT_STRING + (ClientIdServer.instance()).getId();
  }

  public String getName() {
    return name;
  }
  public String getPhone() {
    return phone;
  }
  public String getAddress() {
    return address;
  }
  public String getId() {
    return id;
  }
  public void setName(String newName) {
    name = newName;
  }
  public void setAddress(String newAddress) {
    address = newAddress;
  }
  public void setPhone(String newPhone) {
    phone = newPhone;
  }
  public boolean equals(String id) {
    return this.id.equals(id);
  }
  public String toString() {
    String string = " Id: " + id + " Name: " + name + " Address: " + address + " Phone: " + phone;
    return string;
  }

  public void receivePayment(double amount) {
    balance += amount;
    transactions.add(new Transaction(Transaction.Type.PAYMENT, amount));
  }

  public double getBalance() {
    return balance;
  }

  public void debit(double amount) {
    balance -= amount;
    transactions.add(new Transaction(Transaction.Type.ORDER, amount));
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void addOrderTransaction(String productId, int quantity, double totalCost) {
    transactions.add(new Transaction(Transaction.Type.ORDER, productId, quantity, totalCost));
  }

  public void addWaitlistFulfillmentTransaction(String productId, int quantity, double totalCost) {
    transactions.add(new Transaction(Transaction.Type.WAITLIST_FULFILLMENT, productId, quantity, totalCost));
  }
}
