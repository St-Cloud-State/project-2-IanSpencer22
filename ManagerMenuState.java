import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.Iterator;

public class ManagerMenuState implements State {
    private Context context;

    public ManagerMenuState(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        JFrame frame = context.getFrame();
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());

        JButton addProductButton = new JButton("Add Product");
        JButton displayWaitlistButton = new JButton("Display Waitlist For Product");
        JButton receiveShipmentButton = new JButton("Receive Shipment");
        JButton becomeClerkButton = new JButton("Become Clerk");
        JButton logoutButton = new JButton("Logout");

        addProductButton.addActionListener(e -> addProduct());
        displayWaitlistButton.addActionListener(e -> displayWaitlist());
        receiveShipmentButton.addActionListener(e -> receiveShipment());
        becomeClerkButton.addActionListener(e -> {
            context.changeState(Context.CLERK_MENU_STATE);
            context.getFrame().revalidate();
            context.getFrame().repaint();
        });
        logoutButton.addActionListener(e -> {
            context.logout();
            context.getFrame().revalidate();
            context.getFrame().repaint();
        });

        frame.add(becomeClerkButton);
        frame.add(addProductButton);
        frame.add(displayWaitlistButton);
        frame.add(receiveShipmentButton);
        frame.add(becomeClerkButton);
        frame.add(logoutButton);

        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void addProduct() {
        String name = JOptionPane.showInputDialog(context.getFrame(), "Enter product name:");
        String id = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID:");
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(context.getFrame(), "Enter product quantity:"));
        double price = Double.parseDouble(JOptionPane.showInputDialog(context.getFrame(), "Enter product price:"));
        Warehouse.instance().addProduct(name, id, quantity, price);
        JOptionPane.showMessageDialog(context.getFrame(), "Product added.");
    }

    private void displayWaitlist() {
        String productId = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID:");
        StringBuilder waitlist = new StringBuilder("Waitlist for Product ID " + productId + ":\n");
        Iterator<WaitlistItem> waitlistItems = Warehouse.instance().getWaitlistForProduct(productId);
        while (waitlistItems.hasNext()) {
            WaitlistItem item = waitlistItems.next();
            waitlist.append("Client ID: ").append(item.getClientId())
                    .append("| Product ID: ").append(item.getProductId())
                    .append("| Quantity: ").append(item.getQuantity())
                    .append("\n");
        }
        JOptionPane.showMessageDialog(context.getFrame(), waitlist.toString());
    }

    private void receiveShipment() {
        String productId = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID:");
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(context.getFrame(), "Enter quantity received:"));
        Warehouse.instance().receiveShipment(productId, quantity);
        JOptionPane.showMessageDialog(context.getFrame(), "Shipment received.");
    }
} 