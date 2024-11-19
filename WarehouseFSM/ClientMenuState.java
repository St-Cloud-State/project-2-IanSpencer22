import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class ClientMenuState implements State {
    private Context context;

    public ClientMenuState(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        String clientId = context.getCurrentClientId();
        if (clientId == null) {
            clientId = JOptionPane.showInputDialog(context.getFrame(), "Enter client ID:");
            if (Warehouse.instance().getClient(clientId) != null) {
                context.setCurrentClientId(clientId);
            } else {
                JOptionPane.showMessageDialog(context.getFrame(), "Invalid client ID.");
                context.logout();
                return;
            }
        }

        JFrame frame = context.getFrame();
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.setTitle("WMS: Client Menu");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JLabel welcomeLabel = new JLabel("Welcome, " + Warehouse.instance().getClient(clientId).getName() + " (Client ID: " + clientId + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(welcomeLabel, gbc);

        // Load and resize icons
        ImageIcon detailsIcon = new ImageIcon(new ImageIcon("assets/detailsIcon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ImageIcon logoutIcon = new ImageIcon(new ImageIcon("assets/logoutIcon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        JButton showDetailsButton = new JButton("Client Details", detailsIcon);
        JButton showProductsButton = new JButton("Product List");
        JButton showTransactionsButton = new JButton("Transactions");
        JButton addToWishlistButton = new JButton("Add to Wishlist");
        JButton showWishlistButton = new JButton("Display Wishlist");
        JButton placeOrderButton = new JButton("Place Order");
        JButton logoutButton = new JButton("Logout", logoutIcon);

        JButton[] buttons = {showDetailsButton, showProductsButton, showTransactionsButton, 
                             addToWishlistButton, showWishlistButton, placeOrderButton, logoutButton};

        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(200, 40));
        }

        showDetailsButton.addActionListener(e -> showClientDetails());
        showProductsButton.addActionListener(e -> showProducts());
        showTransactionsButton.addActionListener(e -> showTransactions());
        addToWishlistButton.addActionListener(e -> addToWishlist());
        showWishlistButton.addActionListener(e -> showWishlist());
        placeOrderButton.addActionListener(e -> placeOrder());
        logoutButton.addActionListener(e -> {
            context.logout();
            context.getFrame().revalidate();
            context.getFrame().repaint();
        });

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(showDetailsButton, gbc);
        gbc.gridx = 1;
        panel.add(showProductsButton, gbc);
        gbc.gridx = 2;
        panel.add(showTransactionsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(addToWishlistButton, gbc);
        gbc.gridx = 1;
        panel.add(showWishlistButton, gbc);
        gbc.gridx = 2;
        panel.add(placeOrderButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(logoutButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void showClientDetails() {
        String clientId = context.getCurrentClientId();
        Client client = Warehouse.instance().getClient(clientId);
        if (client != null) {
            JOptionPane.showMessageDialog(context.getFrame(),
                "<html><div style='text-align: center; font-size:14px;'><b>Client ID:</b> " + client.getId() +
                "<br><b>Name:</b> " + client.getName() +
                "<br><b>Address:</b> " + client.getAddress() +
                "<br><b>Phone:</b> " + client.getPhone() +
                "<br><b>Balance:</b> $" + client.getBalance() + "</div></html>");
        } else {
            JOptionPane.showMessageDialog(context.getFrame(), "Client not found.");
        }
    }

    private void showProducts() {
        StringBuilder productList = new StringBuilder("<html><div style='text-align: center; font-size:14px;'><b>Products:</b><br>");
        Iterator<Product> products = Warehouse.instance().getProducts();
        while (products.hasNext()) {
            Product product = products.next();
            productList.append("<b>ID:</b> ").append(product.getId())
                       .append(" | <b>Name:</b> ").append(product.getName())
                       .append(" | <b>Quantity:</b> ").append(product.getQuantity())
                       .append(" | <b>Price:</b> $").append(product.getPrice())
                       .append("<br>");
        }
        productList.append("</div></html>");
        JOptionPane.showMessageDialog(context.getFrame(), productList.toString());
    }

    private void showTransactions() {
        String clientId = context.getCurrentClientId();
        Client client = Warehouse.instance().getClient(clientId);
        if (client != null) {
            StringBuilder transactionList = new StringBuilder("<html><div style='text-align: center; font-size:14px;'><b>---- Transactions -----</b><br>");

            // Processed Orders
            transactionList.append("<br><b>Processed Orders:</b><br>");
            List<Transaction> transactions = client.getTransactions();
            double totalCost = 0.0;
            double totalPayments = 0.0;

            for (Transaction transaction : transactions) {
                if (transaction.getType() == Transaction.Type.ORDER && transaction.getProductId() != null) {
                    transactionList.append("<b>Product ID:</b> ").append(transaction.getProductId())
                                   .append(", <b>Quantity:</b> ").append(transaction.getQuantity())
                                   .append(", <b>Total Cost:</b> $").append(transaction.getTotalCost())
                                   .append("<br>");
                    totalCost += transaction.getTotalCost();
                }
            }

            // Fulfilled Waitlist Items
            transactionList.append("<br><b>Fulfilled Waitlist Items:</b><br>");
            for (Transaction transaction : transactions) {
                if (transaction.getType() == Transaction.Type.WAITLIST_FULFILLMENT && transaction.getProductId() != null) {
                    transactionList.append("<b>Product ID:</b> ").append(transaction.getProductId())
                                   .append(", <b>Quantity:</b> ").append(transaction.getQuantity())
                                   .append(", <b>Total Cost:</b> $").append(transaction.getTotalCost())
                                   .append("<br>");
                    totalCost += transaction.getTotalCost();
                }
            }

            // Payments Received
            transactionList.append("<br><b>Payments Received:</b><br>");
            for (Transaction transaction : transactions) {
                if (transaction.getType() == Transaction.Type.PAYMENT) {
                    transactionList.append("<b>Payment Amount:</b> $").append(transaction.getAmount())
                                   .append("<br>");
                    totalPayments += transaction.getAmount();
                }
            }

            // Summary
            transactionList.append("<br><b>Summary:</b><br>");
            transactionList.append("<b>Total Cost of Orders and Fulfilled Waitlist Items:</b> $").append(totalCost).append("<br>");
            transactionList.append("<b>Total Payments Received:</b> $").append(totalPayments).append("<br>");
            transactionList.append("<b>Account Balance:</b> $").append(totalPayments - totalCost).append("<br>");
            transactionList.append("</div></html>");

            JOptionPane.showMessageDialog(context.getFrame(), transactionList.toString());
        } else {
            JOptionPane.showMessageDialog(context.getFrame(), "Client not found.");
        }
    }

    private void addToWishlist() {
        String productId = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID to add to wishlist:");
        if (productId == null || productId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Product ID cannot be empty.");
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(context.getFrame(), "Enter quantity:");
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Quantity cannot be empty.");
            return;
        }
        int quantity = Integer.parseInt(quantityStr);

        String clientId = context.getCurrentClientId();

        try {
            Warehouse.instance().addItemToWishlist(clientId, productId, quantity);
            JOptionPane.showMessageDialog(context.getFrame(), "Product added to wishlist.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(context.getFrame(), "Failed to add product to wishlist: " + e.getMessage());
        }
    }

    private void showWishlist() {
        String clientId = context.getCurrentClientId();
        StringBuilder wishlist = new StringBuilder("<html><div style='text-align: center; font-size:14px;'><b>Wishlist:</b><br>");
        Iterator<WishlistItem> items = Warehouse.instance().getWishlistItemsForClient(clientId);
        while (items.hasNext()) {
            WishlistItem item = items.next();
            wishlist.append(item).append("<br>");
        }
        wishlist.append("</div></html>");
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
                        "<html><div style='text-align: center; font-size:14px;'><b>Product ID:</b> " + item.getProductId() +
                        "<br><b>Quantity:</b> " + item.getQuantity() +
                        "<br><b>Price:</b> $" + product.getPrice() +
                        "<br>Enter quantity to order (or 0 to skip):</div></html>"));
                if (quantity > 0) {
                    orderDetails.put(item.getProductId(), quantity);
                }
            }
        }

        totalCost = Warehouse.instance().processClientOrder(clientId, orderDetails);
        JOptionPane.showMessageDialog(context.getFrame(), "Order placed. Total cost: $" + totalCost);
    }
} 