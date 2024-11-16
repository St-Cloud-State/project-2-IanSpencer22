import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Context {
    private static Context context;
    private JFrame frame;
    private State[] states;
    private int[][] nextState;
    private int currentState;
    private String currentClientId;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static final int OPENING_STATE = 0;
    public static final int CLIENT_MENU_STATE = 1;
    public static final int CLERK_MENU_STATE = 2;
    public static final int MANAGER_MENU_STATE = 3;

    private Context() {
        states = new State[4];
        states[OPENING_STATE] = new OpeningState(this);
        states[CLIENT_MENU_STATE] = new ClientMenuState(this);
        states[CLERK_MENU_STATE] = new ClerkMenuState(this);
        states[MANAGER_MENU_STATE] = new ManagerMenuState(this);

        nextState = new int[4][4];

        nextState[OPENING_STATE][0] = CLIENT_MENU_STATE;
        nextState[OPENING_STATE][1] = CLERK_MENU_STATE;
        nextState[OPENING_STATE][2] = MANAGER_MENU_STATE;

        currentState = OPENING_STATE;

        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Warehouse Management System");
        frame.addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static Context instance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    public void changeState(int transition) {
        currentState = nextState[currentState][transition];
        states[currentState].run();
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setCurrentClientId(String clientId) {
        this.currentClientId = clientId;
    }

    public String getCurrentClientId() {
        return currentClientId;
    }

    public void process() {
        states[currentState].run();
    }

    public static void main(String[] args) {
        Context.instance().process();
    }
} 