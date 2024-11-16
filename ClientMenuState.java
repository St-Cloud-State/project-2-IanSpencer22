import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;

public class ClientMenuState implements State {
    private Context context;

    public ClientMenuState(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        JFrame frame = context.getFrame();
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());

        JButton showDetailsButton = new JButton("Show Client Details");
        JButton showProductsButton = new JButton("Show Products");
        JButton showTransactionsButton = new JButton("Show Transactions");
        JButton addToWishlistButton = new JButton("Add to Wishlist");
        JButton showWishlistButton = new JButton("Show Wishlist");
        JButton placeOrderButton = new JButton("Place Order");
        JButton logoutButton = new JButton("Logout");

        // Add action listeners for each button
        showDetailsButton.addActionListener(e -> showClientDetails());
        showProductsButton.addActionListener(e -> showProducts());
        showTransactionsButton.addActionListener(e -> showTransactions());
        addToWishlistButton.addActionListener(e -> addToWishlist());
        showWishlistButton.addActionListener(e -> showWishlist());
        placeOrderButton.addActionListener(e -> placeOrder());
        logoutButton.addActionListener(e -> context.changeState(Context.OPENING_STATE));

        frame.add(showDetailsButton);
        frame.add(showProductsButton);
        frame.add(showTransactionsButton);
        frame.add(addToWishlistButton);
        frame.add(showWishlistButton);
        frame.add(placeOrderButton);
        frame.add(logoutButton);

        frame.setVisible(true);
    }

    private void showClientDetails() {
        String clientId = context.getCurrentClientId();
        Client client = Warehouse.instance().getClient(clientId);
        if (client != null) {
            JOptionPane.showMessageDialog(context.getFrame(),
                "Client ID: " + client.getId() +
                "\nName: " + client.getName() +
                "\nAddress: " + client.getAddress() +
                "\nPhone: " + client.getPhone() +
                "\nBalance: $" + client.getBalance());
        } else {
            JOptionPane.showMessageDialog(context.getFrame(), "Client not found.");
        }
    }

    private void showProducts() {
        StringBuilder productList = new StringBuilder("Products:\n");
        Iterator<Product> products = Warehouse.instance().getProducts();
        while (products.hasNext()) {
            Product product = products.next();
            productList.append("ID: ").append(product.getId())
                       .append(", Name: ").append(product.getName())
                       .append(", Price: $").append(product.getPrice())
                       .append("\n");
        }
        JOptionPane.showMessageDialog(context.getFrame(), productList.toString());
    }

    private void showTransactions() {
        String clientId = context.getCurrentClientId();
        Client client = Warehouse.instance().getClient(clientId);
        if (client != null) {
            StringBuilder transactionList = new StringBuilder("Transactions:\n");
            for (Transaction transaction : client.getTransactions()) {
                transactionList.append(transaction).append("\n");
            }
            JOptionPane.showMessageDialog(context.getFrame(), transactionList.toString());
        } else {
            JOptionPane.showMessageDialog(context.getFrame(), "Client not found.");
        }
    }

    private void addToWishlist() {
        String productId = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID to add to wishlist:");
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(context.getFrame(), "Enter quantity:"));
        String clientId = context.getCurrentClientId();
        if (Warehouse.instance().addItemToWishlist(clientId, productId, quantity)) {
            JOptionPane.showMessageDialog(context.getFrame(), "Product added to wishlist.");
        } else {
            JOptionPane.showMessageDialog(context.getFrame(), "Failed to add product to wishlist.");
        }
    }

    private void showWishlist() {
        String clientId = context.getCurrentClientId();
        StringBuilder wishlist = new StringBuilder("Wishlist:\n");
        Iterator<WishlistItem> items = Warehouse.instance().getWishlistItemsForClient(clientId);
        while (items.hasNext()) {
            WishlistItem item = items.next();
            wishlist.append(item).append("\n");
        }
        JOptionPane.showMessageDialog(context.getFrame(), wishlist.toString());
    }

    private void placeOrder() {
        String clientId = context.getCurrentClientId();
        Map<String, Integer> orderDetails = new HashMap<>();
        double totalCost = 0.0;

        Iterator<WishlistItem> wishlistItems = Warehouse.instance().getWishlistItemsForClient(clientId);
        while (wishlistItems.hasNext()) {
            WishlistItem item = wishlistItems.next();
            Product product = Warehouse.instance().getProduct(item.getProductId());
            if (product != null) {
                int quantity = Integer.parseInt(JOptionPane.showInputDialog(context.getFrame(),
                        "Product ID: " + item.getProductId() +
                        "\nQuantity: " + item.getQuantity() +
                        "\nPrice: $" + product.getPrice() +
                        "\nEnter quantity to order (or 0 to skip):"));
                if (quantity > 0) {
                    orderDetails.put(item.getProductId(), quantity);
                }
            }
        }

        totalCost = Warehouse.instance().processClientOrder(clientId, orderDetails);
        JOptionPane.showMessageDialog(context.getFrame(), "Order placed. Total cost: $" + totalCost);
    }
} 