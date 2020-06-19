import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class CreateNewUserPane extends JPanel {

    private final ClientMain client;

    //private ArrayList<String> users = new ArrayList<>();
    //private ArrayList<String> passwords = new ArrayList<>();

    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton createButton = new JButton("Create user");

    CreateNewUserPane() throws IOException {
        //super("Create new user");
        this.client = new ClientMain("localhost", 1234);
        client.connect();

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(loginField);
        jPanel.add(passwordField);
        jPanel.add(createButton);

         */

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(loginField);
        add(passwordField);
        add(createButton);

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
        /*
        getContentPane().add(jPanel, BorderLayout.CENTER);
        setSize(300,200);
        //pack(); //sizes the window to fit all the components automatically
        setLocationRelativeTo(null);
        setVisible(true);

         */


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

                /*ClientStart clientStart = new ClientStart();
                JFrame frame = new JFrame("Main menu");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300,200);
                frame.getContentPane().add(clientStart, BorderLayout.CENTER);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                setVisible(false);*/
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
