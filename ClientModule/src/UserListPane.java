import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class UserListPane extends JPanel implements UserStatusListener {


    private final ClientMain client;

    private JPanel pane1, pane2;

    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> userListUI = new JList<>(userListModel);


    private JButton chatroomButton = new JButton("Create chatroom");
    private JTextField chatroomName = new JTextField("");
    private JButton logoutButton = new JButton("Logout");

    public UserListPane(ClientMain client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        //userListModel = new DefaultListModel<>();
        //userListUI = new JList<>(userListModel);

        pane1 = new JPanel();
        pane2 = new JPanel();


        //pane1.setLayout(new BorderLayout());
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.PAGE_AXIS));
        pane1.add(new JScrollPane(userListUI), BorderLayout.CENTER);

        pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
        pane2.add(chatroomName, BorderLayout.SOUTH);
        pane2.add(chatroomButton, BorderLayout.SOUTH);
        pane2.add(logoutButton, BorderLayout.PAGE_END);


        add(pane1, BorderLayout.PAGE_START);
        add(pane2, BorderLayout.PAGE_END);


        chatroomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doCreateChatroom();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.logoff();
                    dispose();

                    //client.logoff();
                    LoginPane loginPane = new LoginPane();
                    JFrame frame = new JFrame("Login");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.getContentPane().add(loginPane, BorderLayout.CENTER);
                    frame.setSize(300,200);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);


                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
            }
        });


        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = userListUI.getSelectedValue();
                    MessagePane messagePane = new MessagePane(client, login);

                    JFrame frame2 = new JFrame("Message: " + login);

                    frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame2.setSize(500,500);
                    frame2.getContentPane().add(messagePane, BorderLayout.CENTER);
                    frame2.setLocationRelativeTo(null);
                    frame2.setVisible(true);
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    System.out.println("check here");
                    client.logoff();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void doCreateChatroom() throws IOException {

        String name = chatroomName.getText();
        client.join("#" + name);
        online("#" + name);


    }

    public void dispose() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.dispose();
    }

    public static void main(String[] args) throws IOException {
        ClientMain client = new ClientMain("localhost", 1234);

        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(400,600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        if (client.connect()) {
            try {
                client.login("guest", "guest");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
