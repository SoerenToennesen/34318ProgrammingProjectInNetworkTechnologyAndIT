import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class CreateNewUserPane extends JPanel {

    private final ClientMain client;

    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton createButton = new JButton("Create user");
    JButton backButton = new JButton("Back");

    CreateNewUserPane() {
        this.client = new ClientMain("localhost", 1234);
        client.connect();
        
        setLayout(new FlowLayout());
        add(loginField);
        add(passwordField);
        add(createButton);
        add(backButton);
        
        loginField.setPreferredSize(new Dimension(200,40));
        
        passwordField.setPreferredSize(new Dimension(200,40));
        
        createButton.setPreferredSize(new Dimension(200,30));
        
        backButton.setPreferredSize(new Dimension(200, 20));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        
        createButton.addActionListener(e -> doCreateUser());
        
        
        backButton.addActionListener(e -> {
            //dispose();
            setVisibleParentFrame();
            ClientStart clientStart = new ClientStart();
            clientStart.setVisible(true);
        });        

        loginField.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    doCreateUser();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    doCreateUser();
                }
            }
        });


    }

    private void doCreateUser() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        //String password = passwordField.getText();

        try {
            if (login.equals("") || password.equals("")) {
                loginField.setText("");
                passwordField.setText("");
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
            } else if (login.contains(" ") || password.contains(" ")) {
                loginField.setText("");
                passwordField.setText("");
                JOptionPane.showMessageDialog(this, "Username/password can't contain spaces");
            } else if (client.create(login, password)) {
                setVisibleParentFrame();
                ClientStart clientStart = new ClientStart();
                clientStart.setVisible(true);
            } else {

                loginField.setText("");
                passwordField.setText("");
                JOptionPane.showMessageDialog(this, "Username already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.dispose();
    }

    public void setVisibleParentFrame() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.setVisible(false);
    }

    public static void main(String[] args) {
        /*CreateNewUserPane createNewUserPane = new CreateNewUserPane();
        createNewUserPane.setVisible(true);*/

        CreateNewUserPane createNewUserPane = new CreateNewUserPane();
        JFrame frame = new JFrame("Register user");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,200);
        frame.getContentPane().add(createNewUserPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}
