import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class CreateNewUserPane extends JPanel {

    private final ClientMain client;

    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton createButton = new JButton("Create user");
    JButton backButton = new JButton("Back");

    CreateNewUserPane() throws IOException {
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
        

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doCreateUser();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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

    }

    private void doCreateUser() throws IOException {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (login.equals("") || password.equals("")) {
                loginField.setText("");
                passwordField.setText("");
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
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

    public static void main(String[] args) throws IOException {
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
