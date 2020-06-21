import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LoginPane extends JPanel {

    private final ClientMain client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    JButton backButton = new JButton("Back");

    public LoginPane() throws IOException {
        
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


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    //dispose();
                    setVisibleParentFrame();


                    ClientStart clientStart = new ClientStart();
                    clientStart.setVisible(true);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
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

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (login.equals("") || password.equals("")) {

            } else if (client.login(login, password)) {

                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(550,270);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login/password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setVisibleParentFrame() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.setVisible(false);
    }

    public static void main(String[] args) throws IOException {


        LoginPane loginPane = new LoginPane();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(410,230);

        frame.getContentPane().add(loginPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
