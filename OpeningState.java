import javax.swing.*;
import java.awt.FlowLayout;
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
        frame.setLayout(new FlowLayout());

        JButton clientButton = new JButton("Client Menu");
        JButton clerkButton = new JButton("Clerk Menu");
        JButton managerButton = new JButton("Manager Menu");

        clientButton.addActionListener(e -> context.changeState(Context.CLIENT_MENU_STATE));
        clerkButton.addActionListener(e -> context.changeState(Context.CLERK_MENU_STATE));
        managerButton.addActionListener(e -> context.changeState(Context.MANAGER_MENU_STATE));

        frame.add(clientButton);
        frame.add(clerkButton);
        frame.add(managerButton);

        frame.setVisible(true);
    }
} 