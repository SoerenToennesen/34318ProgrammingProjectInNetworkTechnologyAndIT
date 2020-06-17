import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientStart extends JFrame {

    //private final ClientMain client;

    JButton loginButton = new JButton("Login");
    JButton createNewUser = new JButton("Create new user");
    JButton exit = new JButton("Exit");


    ClientStart() throws IOException {
        super("Program start");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
        jPanel.add(loginButton);
        jPanel.add(createNewUser);
        jPanel.add(exit);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickLogin();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        createNewUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickCreateNewUser();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        getContentPane().add(jPanel, BorderLayout.CENTER);
        setSize(300,200);
        //pack(); //sizes the window to fit all the components automatically
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void clickLogin() throws IOException {


        try {

            setVisible(false);

            LoginPane loginPane = new LoginPane();
            JFrame frame = new JFrame("Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(loginPane, BorderLayout.CENTER);
            frame.setSize(300,200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clickCreateNewUser() throws IOException {


        try {

            setVisible(false);

            CreateNewUserPane createNewUserPane = new CreateNewUserPane();
            JFrame frame = new JFrame("Create new user");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(createNewUserPane, BorderLayout.CENTER);
            frame.setSize(300,200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ClientStart clientStart = new ClientStart();
        clientStart.setVisible(true);

    }
}
