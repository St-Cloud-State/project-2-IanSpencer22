import javax.swing.*;
import java.awt.*;
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
        frame.setLayout(new BorderLayout());
        frame.setTitle("WMS: Manager Menu");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        ImageIcon logoutIcon = new ImageIcon(new ImageIcon("assets/logoutIcon.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        JButton addProductButton = new JButton("Add Product");
        JButton displayWaitlistButton = new JButton("Display Waitlist");
        JButton receiveShipmentButton = new JButton("Receive Shipment");
        JButton becomeClerkButton = new JButton("Become Clerk");
        JButton logoutButton = new JButton("Logout", logoutIcon);

        JButton[] buttons = {addProductButton, displayWaitlistButton, receiveShipmentButton, 
                             becomeClerkButton, logoutButton};

        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(200, 40));
        }

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(addProductButton, gbc);
        gbc.gridx = 1;
        panel.add(displayWaitlistButton, gbc);
        gbc.gridx = 2;
        panel.add(receiveShipmentButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(becomeClerkButton, gbc);
        gbc.gridx = 1;
        panel.add(logoutButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void addProduct() {
        String name = JOptionPane.showInputDialog(context.getFrame(), "Enter product name:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Product name cannot be empty.");
            return;
        }

        String id = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID:");
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Product ID cannot be empty.");
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(context.getFrame(), "Enter product quantity:");
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Product quantity cannot be empty.");
            return;
        }
        int quantity = Integer.parseInt(quantityStr);

        String priceStr = JOptionPane.showInputDialog(context.getFrame(), "Enter product price:");
        if (priceStr == null || priceStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(context.getFrame(), "Product price cannot be empty.");
            return;
        }
        double price = Double.parseDouble(priceStr);

        Warehouse.instance().addProduct(name, id, quantity, price);
        JOptionPane.showMessageDialog(context.getFrame(), "Product added.");
    }

    private void displayWaitlist() {
        String productId = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID:");
        StringBuilder waitlist = new StringBuilder("<html><div style='text-align: center; font-size:14px;'><b>Waitlist for Product ID:</b> " + productId + "<br>");
        Iterator<WaitlistItem> items = Warehouse.instance().getWaitlistForProduct(productId);
        while (items.hasNext()) {
            WaitlistItem item = items.next();
            waitlist.append("<b>Client ID:</b> ").append(item.getClientId())
                    .append(" | <b>Quantity:</b> ").append(item.getQuantity())
                    .append("<br>");
        }
        waitlist.append("</div></html>");
        JOptionPane.showMessageDialog(context.getFrame(), waitlist.toString());
    }

    private void receiveShipment() {
        String productId = JOptionPane.showInputDialog(context.getFrame(), "Enter product ID:");
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(context.getFrame(), "Enter quantity received:"));
        Warehouse.instance().receiveShipment(productId, quantity);
        JOptionPane.showMessageDialog(context.getFrame(), "Shipment received.");
    }
} 