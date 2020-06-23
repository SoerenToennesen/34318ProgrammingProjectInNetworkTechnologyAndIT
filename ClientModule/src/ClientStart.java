import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ClientStart extends JFrame {

    JButton loginButton = new JButton("Login");
    JButton createNewUser = new JButton("Create new user");
    JButton exit = new JButton("Exit");


    ClientStart() {
        super("Main menu");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.black);
        jPanel.setLayout(new BorderLayout());
        jPanel.add(loginButton,BorderLayout.WEST);
        jPanel.add(createNewUser,BorderLayout.EAST);
        jPanel.add(exit,BorderLayout.SOUTH);
        
        loginButton.setToolTipText("Login with your email and password");
        //loginButton.setBackground(Color.BLACK);
        loginButton.setPreferredSize(new Dimension(200, 50));
        //loginButton.setForeground(Color.WHITE);
  
        
        createNewUser.setToolTipText("Create a new user");
        //createNewUser.setBackground(Color.BLACK);
        createNewUser.setPreferredSize(new Dimension(200, 50));
        //createNewUser.setForeground(Color.WHITE);
        
        exit.setToolTipText("Exit the application");
        exit.setBackground(Color.BLACK);
        exit.setForeground(Color.WHITE);
        //exit.setPreferredSize(new Dimension(1, 31));

        loginButton.addActionListener(e -> clickLogin());

        createNewUser.addActionListener(e -> clickCreateNewUser());

        exit.addActionListener(e -> {
            System.exit(0);
        });

        loginButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    clickLogin();
                }
            }
        });


        getContentPane().add(jPanel, BorderLayout.CENTER);
        setSize(410,200);
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

    private void clickLogin() {


        setVisible(false);

        LoginPane loginPane = new LoginPane();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(loginPane, BorderLayout.CENTER);
        frame.setSize(300,200);
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
        frame.setSize(300,200);
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
