import javax.swing.*;
import java.awt.FlowLayout;
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
        frame.setLayout(new FlowLayout());

        JButton addClientButton = new JButton("Add Client");
        JButton showProductsButton = new JButton("Show Products");
        JButton showClientsButton = new JButton("Show Clients");
        JButton showClientsWithBalanceButton = new JButton("Show Clients with Balance");
        JButton recordPaymentButton = new JButton("Record Payment");
        JButton becomeClientButton = new JButton("Become Client");
        JButton logoutButton = new JButton("Logout");

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

        frame.add(addClientButton);
        frame.add(showProductsButton);
        frame.add(showClientsButton);
        frame.add(showClientsWithBalanceButton);
        frame.add(recordPaymentButton);
        frame.add(becomeClientButton);
        frame.add(logoutButton);

        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void addClient() {
        String name = JOptionPane.showInputDialog(context.getFrame(), "Enter client name:");
        String address = JOptionPane.showInputDialog(context.getFrame(), "Enter client address:");
        String phone = JOptionPane.showInputDialog(context.getFrame(), "Enter client phone:");
        Warehouse.instance().addClient(name, address, phone);
        JOptionPane.showMessageDialog(context.getFrame(), "Client added.");
    }

    private void showProducts() {
        StringBuilder productList = new StringBuilder("Products:\n");
        Iterator<Product> products = Warehouse.instance().getProducts();
        while (products.hasNext()) {
            Product product = products.next();
            productList.append("ID: ").append(product.getId())
                       .append("| Name: ").append(product.getName())
                       .append("| Price: $").append(product.getPrice())
                       .append("| Quantity: ").append(product.getQuantity())
                       .append("\n");
        }
        JOptionPane.showMessageDialog(context.getFrame(), productList.toString());
    }

    private void showClients() {
        StringBuilder clientList = new StringBuilder("Clients:\n");
        Iterator<Client> clients = Warehouse.instance().getAllClients();
        while (clients.hasNext()) {
            Client client = clients.next();
            clientList.append("ID: ").append(client.getId())
                      .append("| Name: ").append(client.getName())
                      .append("\n");
        }
        JOptionPane.showMessageDialog(context.getFrame(), clientList.toString());
    }

    private void showClientsWithBalance() {
        StringBuilder clientList = new StringBuilder("Clients with Outstanding Balance:\n");
        Iterator<Client> clients = Warehouse.instance().getAllClients();
        while (clients.hasNext()) {
            Client client = clients.next();
            if (client.getBalance() < 0) {
                clientList.append("ID: ").append(client.getId())
                          .append("| Name: ").append(client.getName())
                          .append("| Balance: $").append(client.getBalance())
                          .append("\n");
            }
        }
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