import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LoginPane extends JPanel {

    private final ClientMain client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    JButton backButton = new JButton("Back");

    public LoginPane() {
        
        this.client = new ClientMain("localhost", 1234);
        client.connect(); 


        setLayout(new FlowLayout());
        add(loginField);
        add(passwordField);
        add(loginButton);
        add(backButton);
        
        loginField.setPreferredSize(new Dimension(200,40));
        
        passwordField.setPreferredSize(new Dimension(200,40));
        
        loginButton.setPreferredSize(new Dimension(200,30));
        
        backButton.setPreferredSize(new Dimension(200, 20));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);


        loginButton.addActionListener(e -> doLogin());

        backButton.addActionListener(e -> {

            //dispose();
            setVisibleParentFrame();


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


        //checking commit



    }

    private void doLogin() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        //String password = passwordField.getText();

        try {
            if (login.equals("") || password.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter a username/password");
            } else if (client.login(login, password)) {
                client.setMyName(login);
                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600,280);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

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

    public static void main(String[] args) {


        LoginPane loginPane = new LoginPane();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(410,230);

        frame.getContentPane().add(loginPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
