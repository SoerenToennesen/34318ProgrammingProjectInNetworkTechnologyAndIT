import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class CreateNewUserPane extends JFrame {

    private final ClientMain client;

    //private ArrayList<String> users = new ArrayList<>();
    //private ArrayList<String> passwords = new ArrayList<>();

    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton createButton = new JButton("Create user");

    CreateNewUserPane() throws IOException {
        super("Create new user");
        this.client = new ClientMain("localhost", 8818);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(loginField);
        jPanel.add(passwordField);
        jPanel.add(createButton);

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

        getContentPane().add(jPanel, BorderLayout.CENTER);
        setSize(300,200);
        //pack(); //sizes the window to fit all the components automatically
        setLocationRelativeTo(null);
        setVisible(true);


    }

    private void doCreateUser() throws IOException {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.create(login, password)) {

                setVisible(false);

                System.out.println("hellothereeverybody4");
                ClientStart clientStart = new ClientStart();
                JFrame frame = new JFrame("Main menu");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300,200);
                System.out.println("hellothereeverybody5");
                frame.getContentPane().add(clientStart, BorderLayout.CENTER);
                System.out.println("hellothereeverybody6");
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                System.out.println("hellothereeverybody1");

                setVisible(false);

                System.out.println("hellothereeverybody2");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists");
            }
        } catch (IOException e) {
            System.out.println("hellothereeverybody3");
            e.printStackTrace();
        }




    }
    public static void main(String[] args) throws IOException {
        CreateNewUserPane createNewUserPane = new CreateNewUserPane();
        createNewUserPane.setVisible(true);

    }

}
