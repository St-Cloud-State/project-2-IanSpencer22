import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Stack;

public class Context {
    private static Context context;
    private JFrame frame;
    private State[] states;
    private int[][] nextState;
    private int currentState;
    private Stack<Integer> stateStack = new Stack<>();
    private String currentClientId;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private int currentUser;
    private String userID;

    public static final int OPENING_STATE = 0;
    public static final int CLIENT_MENU_STATE = 1;
    public static final int CLERK_MENU_STATE = 2;
    public static final int MANAGER_MENU_STATE = 3;

    private Context() {
        if (yesOrNo("Look for saved data and use it?")) {
            retrieve();
        } else {
            warehouse = Warehouse.instance();
        }

        states = new State[4];
        states[OPENING_STATE] = new OpeningState(this);
        states[CLIENT_MENU_STATE] = new ClientMenuState(this);
        states[CLERK_MENU_STATE] = new ClerkMenuState(this);
        states[MANAGER_MENU_STATE] = new ManagerMenuState(this);

        nextState = new int[4][4];
        nextState[OPENING_STATE][1] = CLIENT_MENU_STATE;
        nextState[OPENING_STATE][2] = CLERK_MENU_STATE;
        nextState[OPENING_STATE][3] = MANAGER_MENU_STATE;

        nextState[CLERK_MENU_STATE][1] = CLIENT_MENU_STATE;
        nextState[MANAGER_MENU_STATE][2] = CLERK_MENU_STATE;

        currentState = OPENING_STATE;

        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Warehouse Management System (WMS)");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static Context instance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    public void changeState(int transition) {
        stateStack.push(currentState);
        currentState = nextState[currentState][transition];
        if (currentState == -2) {
            System.out.println("Error has occurred");
            terminate();
        }
        if (currentState == -1) {
            terminate();
        }
        states[currentState].run();
    }

    public void logout() {
        currentClientId = null;
        if (!stateStack.isEmpty()) {
            currentState = stateStack.pop();
        } else {
            currentState = OPENING_STATE;
        }
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

    private String getToken(String prompt) {
        do {
            try {
                System.out.println(prompt);
                String line = reader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }
            } catch (IOException ioe) {
                System.exit(0);
            }
        } while (true);
    }

    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no");
        return more.charAt(0) == 'y' || more.charAt(0) == 'Y';
    }

    private void retrieve() {
        try {
            Warehouse tempWarehouse = Warehouse.retrieve();
            if (tempWarehouse != null) {
                System.out.println("The warehouse has been successfully retrieved from the file WarehouseData");
                warehouse = tempWarehouse;
            } else {
                System.out.println("File doesn't exist; creating new warehouse");
                warehouse = Warehouse.instance();
            }
        } catch (Exception cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void terminate() {
        if (yesOrNo("Save data?")) {
            if (warehouse.save()) {
                System.out.println("The warehouse has been successfully saved in the file WarehouseData");
            } else {
                System.out.println("There has been an error in saving");
            }
        }
        System.out.println("Goodbye");
        System.exit(0);
    }

    public void setLogin(int code) {
        currentUser = code;
    }

    public void setUser(String uID) {
        userID = uID;
    }

    public int getLogin() {
        return currentUser;
    }

    public String getUser() {
        return userID;
    }
} 