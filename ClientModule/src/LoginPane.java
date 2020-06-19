import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginPane extends JPanel {

    private final ClientMain client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    JButton backButton = new JButton("Back");

    public LoginPane() throws IOException {
        //super("Login");
        
        this.client = new ClientMain("localhost", 1234);
        client.connect();

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(loginField);
        jPanel.add(passwordField);
        jPanel.add(loginButton);
        jPanel.add(backButton);

         */


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(loginField);
        add(passwordField);
        add(loginButton);
        add(backButton);


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


        /*getContentPane().add(jPanel, BorderLayout.CENTER);
        setSize(300,200);
        //pack(); //sizes the window to fit all the components automatically
        setLocationRelativeTo(null);
        setVisible(true);

         */

        //add(this, BorderLayout.CENTER);
        //setSize(300,200);
        //pack(); //sizes the window to fit all the components automatically
        //setLocationRelativeTo(null);
        //setVisible(true);

    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {
                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400,600);

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
        frame.setSize(300,200);
        frame.getContentPane().add(loginPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        /*
        LoginPane loginPane = new LoginPane();
        loginPane.setVisible(true);

         */

    }
}
