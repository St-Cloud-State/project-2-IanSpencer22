import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class ClerkMenuState implements State {
    private Context context;

    public ClerkMenuState(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        JFrame frame = context.getFrame();
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.setTitle("WMS: Clerk Menu");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        ImageIcon logoutIcon = new ImageIcon(new ImageIcon("assets/logoutIcon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        JButton addClientButton = new JButton("Add Client");
        JButton showProductsButton = new JButton("Product List");
        JButton showClientsButton = new JButton("Client List");
        JButton showClientsWithBalanceButton = new JButton("Balance Due");
        JButton recordPaymentButton = new JButton("Receive Payment");
        JButton becomeClientButton = new JButton("Become Client");
        JButton logoutButton = new JButton("Logout", logoutIcon);

        JButton[] buttons = {addClientButton, showProductsButton, showClientsButton, 
                             showClientsWithBalanceButton, recordPaymentButton, becomeClientButton, logoutButton};

        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(200, 40));
        }

        addClientButton.addActionListener(e -> addClient());
        showProductsButton.addActionListener(e -> showProducts());
        showClientsButton.addActionListener(e -> showClients());
        showClientsWithBalanceButton.addActionListener(e -> showClientsWithBalance());
        recordPaymentButton.addActionListener(e -> recordPayment());
        becomeClientButton.addActionListener(e -> becomeClient());
        logoutButton.addActionListener(e -> {
            context.logout();
            context.getFrame().revalidate();
            context.getFrame().repaint();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(addClientButton, gbc);
        gbc.gridx = 1;
        panel.add(showProductsButton, gbc);
        gbc.gridx = 2;
        panel.add(showClientsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(showClientsWithBalanceButton, gbc);
        gbc.gridx = 1;
        panel.add(recordPaymentButton, gbc);
        gbc.gridx = 2;
        panel.add(becomeClientButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(logoutButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void addClient() {
        String name = JOptionPane.showInputDialog(context.getFrame(), "Enter client name:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Client name cannot be empty.");
            return;
        }

        String address = JOptionPane.showInputDialog(context.getFrame(), "Enter client address:");
        if (address == null || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Client address cannot be empty.");
            return;
        }

        String phone = JOptionPane.showInputDialog(context.getFrame(), "Enter client phone:");
        if (phone == null || phone.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Client phone cannot be empty.");
            return;
        }

        Warehouse.instance().addClient(name, address, phone);
        JOptionPane.showMessageDialog(context.getFrame(), "Client added.");
    }

    private void showProducts() {
        StringBuilder productList = new StringBuilder("<html><div style='font-size:14px;'><b>Products:</b><br>");
        Iterator<Product> products = Warehouse.instance().getProducts();
        while (products.hasNext()) {
            Product product = products.next();
            productList.append("<b>ID:</b> ").append(product.getId())
                       .append(" | <b>Name:</b> ").append(product.getName())
                       .append(" | <b>Price:</b> $").append(product.getPrice())
                       .append(" | <b>Quantity:</b> ").append(product.getQuantity())
                       .append("<br>");
        }
        productList.append("</div></html>");
        JOptionPane.showMessageDialog(context.getFrame(), productList.toString());
    }

    private void showClients() {
        StringBuilder clientList = new StringBuilder("<html><div style='font-size:14px;'><b>Clients:</b><br>");
        Iterator<Client> clients = Warehouse.instance().getAllClients();
        while (clients.hasNext()) {
            Client client = clients.next();
            clientList.append("<b>ID:</b> ").append(client.getId())
                      .append(" | <b>Name:</b> ").append(client.getName())
                      .append("<br>");
        }
        clientList.append("</div></html>");
        JOptionPane.showMessageDialog(context.getFrame(), clientList.toString());
    }

    private void showClientsWithBalance() {
        StringBuilder clientList = new StringBuilder("<html><div style='font-size:14px;'><b>Clients with Outstanding Balance:</b><br>");
        Iterator<Client> clients = Warehouse.instance().getAllClients();
        while (clients.hasNext()) {
            Client client = clients.next();
            if (client.getBalance() < 0) {
                clientList.append("<b>ID:</b> ").append(client.getId())
                          .append(" | <b>Name:</b> ").append(client.getName())
                          .append(" | <b>Balance:</b> $").append(client.getBalance())
                          .append("<br>");
            }
        }
        clientList.append("</div></html>");
        JOptionPane.showMessageDialog(context.getFrame(), clientList.toString());
    }

    private void recordPayment() {
        String clientId = JOptionPane.showInputDialog(context.getFrame(), "Enter client ID:");
        double amount = Double.parseDouble(JOptionPane.showInputDialog(context.getFrame(), "Enter payment amount:"));
        Warehouse.instance().receivePayment(clientId, amount);
        JOptionPane.showMessageDialog(context.getFrame(), "Payment recorded.");
    }

    private void becomeClient() {
        String clientId = JOptionPane.showInputDialog(context.getFrame(), "Enter client ID:");
        if (Warehouse.instance().getClient(clientId) != null) {
            context.setCurrentClientId(clientId);
            context.changeState(Context.CLIENT_MENU_STATE);
        } else {
            JOptionPane.showMessageDialog(context.getFrame(), "Invalid client ID.");
        }
    }
} 