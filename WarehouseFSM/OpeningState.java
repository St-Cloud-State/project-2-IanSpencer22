import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OpeningState implements State {
    private Context context;

    public OpeningState(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        JFrame frame = context.getFrame();
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JButton clientButton = new JButton("Client Menu");
        JButton clerkButton = new JButton("Clerk Menu");
        JButton managerButton = new JButton("Manager Menu");

        JButton[] buttons = {clientButton, clerkButton, managerButton};

        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(200, 40));
        }

        clientButton.addActionListener(e -> context.changeState(Context.CLIENT_MENU_STATE));
        clerkButton.addActionListener(e -> context.changeState(Context.CLERK_MENU_STATE));
        managerButton.addActionListener(e -> context.changeState(Context.MANAGER_MENU_STATE));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(clientButton, gbc);
        gbc.gridx = 1;
        panel.add(clerkButton, gbc);
        gbc.gridx = 2;
        panel.add(managerButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }
} 