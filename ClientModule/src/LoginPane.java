import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class LoginPane extends JPanel {

    private final ClientMain client;
    private JLabel loginLabel = new JLabel("Insert username:");
    private JTextField loginField = new JTextField();
    private JLabel passwordLabel = new JLabel("Insert password:");
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");
    private JButton backButton = new JButton("Back");

    public LoginPane() {
        
        this.client = new ClientMain("localhost", 1234);
        client.connect(); 


        setLayout(new FlowLayout());
        add(loginLabel);
        add(loginField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(backButton);

        loginLabel.setPreferredSize(new Dimension(200,20));
        loginField.setPreferredSize(new Dimension(200,40));
        passwordLabel.setPreferredSize(new Dimension(200,20));
        passwordField.setPreferredSize(new Dimension(200,40));

        loginButton.setToolTipText("Login.");
        loginButton.setPreferredSize(new Dimension(200,30));

        backButton.setToolTipText("Go back to the Start Menu.");
        backButton.setPreferredSize(new Dimension(200, 20));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);


        loginField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loginField.setText("");
            }
        });

        passwordField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordField.setText("");
            }
        });

        loginButton.addActionListener(e -> doLogin());

        backButton.addActionListener(e -> {
            setVisibleParentFrame();
            try {
                doSocketClose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


            ClientStart clientStart = new ClientStart();
            clientStart.setVisible(true);
        });


        loginField.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    doLogin();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    doLogin();
                }
            }
        });
    }

    public void doSocketClose() throws IOException {
        client.logoff();
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try {
            if (login.equals("") || password.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter a username/password");
            } else if (client.login(login, password)) {
                client.setMyName(login);
                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600,290);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.out.println("check here");
                        try {
                            client.logoff();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        System.exit(0);
                    }
                });



                setVisibleParentFrame();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid login/password");
                loginField.setText("");
                passwordField.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setVisibleParentFrame() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.setVisible(false);
    }

    /*public static void main(String[] args) {


        LoginPane loginPane = new LoginPane();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(410,230);

        frame.getContentPane().add(loginPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

     */
}
