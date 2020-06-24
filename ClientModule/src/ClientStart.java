import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ClientStart extends JFrame {

    private JButton loginButton = new JButton("Login");
    private JButton createNewUser = new JButton("Create new user");
    private JButton exit = new JButton("Exit");
    private JLabel closeServerLabel = new JLabel();
    private JTextField closeServerField = new JTextField("");
    private JButton closeServerButton = new JButton("Close server");


    ClientStart() {
        super("Main menu");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.black);
        jPanel.setLayout(new BorderLayout());
        jPanel.add(loginButton,BorderLayout.NORTH);
        jPanel.add(createNewUser,BorderLayout.WEST);
        jPanel.add(exit,BorderLayout.SOUTH);
        jPanel.add(closeServerLabel);
        jPanel.add(closeServerField,BorderLayout.CENTER);
        jPanel.add(closeServerButton,BorderLayout.EAST);

        closeServerField.setText("Admin password...");
        closeServerField.setToolTipText("Close server - requires administrator password");
        //loginButton.setBackground(Color.BLACK);
        closeServerField.setPreferredSize(new Dimension(40, 40));
        //loginButton.setForeground(Color.WHITE);

        closeServerButton.setToolTipText("Close server - requires administrator password");
        //loginButton.setBackground(Color.BLACK);
        closeServerButton.setBackground(new Color(250, 100, 100));
        closeServerButton.setForeground(Color.WHITE);
        closeServerButton.setPreferredSize(new Dimension(110, 40));
        //loginButton.setForeground(Color.WHITE);
        
        loginButton.setToolTipText("Login with your email and password");
        //loginButton.setBackground(Color.BLACK);
        loginButton.setPreferredSize(new Dimension(300, 70));
        //loginButton.setForeground(Color.WHITE);
  
        
        createNewUser.setToolTipText("Create a new user");
        //createNewUser.setBackground(Color.BLACK);
        createNewUser.setPreferredSize(new Dimension(260, 40));
        //createNewUser.setForeground(Color.WHITE);
        
        exit.setToolTipText("Exit the application");
//        exit.setPreferredSize(new Dimension(300,70));
        exit.setBackground(Color.BLACK);
        exit.setForeground(Color.WHITE);
        //exit.setPreferredSize(new Dimension(1, 31));


        closeServerField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeServerField.setText("");
            }
        });

        loginButton.addActionListener(e -> clickLogin());

        createNewUser.addActionListener(e -> clickCreateNewUser());

        exit.addActionListener(e -> System.exit(0));

        loginButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    clickLogin();
                }
            }
        });

        closeServerButton.addActionListener(e -> {
            try {
                initiateCloseServer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        closeServerField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    try {
                        initiateCloseServer();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        getContentPane().add(jPanel, BorderLayout.CENTER);
        setSize(500,180);
        //pack(); //sizes the window to fit all the components automatically
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }

    private void initiateCloseServer() throws IOException {
        if (closeServerField.getText().equalsIgnoreCase("admin")) {
            new CloseServer();
        } else {
            JOptionPane.showMessageDialog(this, "Administrator password to close server incorrect");
            closeServerField.setText("");
        }

    }

    private void clickLogin() {


        setVisible(false);

        LoginPane loginPane = new LoginPane();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(loginPane, BorderLayout.CENTER);
        frame.setSize(300,250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("check here");
                try {
                    loginPane.doSocketClose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });


    }

    private void clickCreateNewUser() {


        setVisible(false);

        CreateNewUserPane createNewUserPane = new CreateNewUserPane();
        JFrame frame = new JFrame("Create new user");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createNewUserPane, BorderLayout.CENTER);
        frame.setSize(300,250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("check here");
                try {
                    createNewUserPane.doSocketClose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });


    }

    public static void main(String[] args) {
        ClientStart clientStart = new ClientStart();
        clientStart.setVisible(true);

    }
}
