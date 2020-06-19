import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class UserListPane extends JPanel implements UserStatusListener, ChatroomStatusListener {


    private final ClientMain client;

    private JPanel pane1, pane2, pane3;

    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> userListUI = new JList<>(userListModel);


    private JLabel usersLabel = new JLabel("Online users:");
    private DefaultListModel<String> chatroomListModel = new DefaultListModel<>();
    private JList<String> chatroomListUI = new JList<>(chatroomListModel);

    private JLabel chatroomsLabel = new JLabel("Available chatrooms:");
    private JButton chatroomButton = new JButton("Create chatroom");
    private JTextField chatroomName = new JTextField("");
    private JButton logoutButton = new JButton("Logout");


    public UserListPane(ClientMain client) throws IOException {
        this.client = client;
        this.client.addUserStatusListener(this);
        this.client.addChatroomStatusListener(this);

        //userListModel = new DefaultListModel<>();
        //userListUI = new JList<>(userListModel);

        pane1 = new JPanel();
        pane2 = new JPanel();
        pane3 = new JPanel();


        //pane1.setLayout(new BorderLayout());
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.PAGE_AXIS));
        pane1.add(usersLabel);
        pane1.add(new JScrollPane(userListUI), BorderLayout.CENTER);

        pane2.setLayout(new BoxLayout(pane2, BoxLayout.PAGE_AXIS));
        pane2.add(chatroomsLabel);
        pane2.add(new JScrollPane(chatroomListUI));
        pane2.add(chatroomName);
        pane2.add(chatroomButton);
        //pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
        pane3.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pane3.add(logoutButton, BorderLayout.SOUTH);


        add(pane1, BorderLayout.PAGE_START);
        add(pane2, BorderLayout.PAGE_END);
        add(pane3, BorderLayout.PAGE_END);


        /*String[] allOnline = client.checkAllOnline();
        for (int i = 0; i < allOnline.length; i++) {
            client.handleOnline(x);
        }

         */

        for (int i = 0; i < chatroomListModel.getSize(); i++) {
            client.join(chatroomListModel.get(i));
        }


        chatroomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> currentChatrooms = new ArrayList<>();
                    for (int i = 0; i < chatroomListModel.getSize(); i++) {
                        currentChatrooms.add(chatroomListModel.get(i));
                    }
                    if (chatroomName.getText().equals("")) {
                        JOptionPane.showMessageDialog(UserListPane.this, "Insert a chatroom name");
                    } else if (currentChatrooms.contains(chatroomName.getText())) {
                        JOptionPane.showMessageDialog(UserListPane.this, "Chatroom already exists");
                        chatroomName.setText("");
                    } else {
                        doCreateChatroom();
                    }
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
                    for (int i = 0; i < chatroomListModel.getSize(); i++) {
                        client.leave(chatroomListModel.get(i));
                    }


                    setVisibleParentFrame();
                    //dispose();

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



        /*logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    client.logoff();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
         */


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

        chatroomListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = chatroomListUI.getSelectedValue();
                    ChatroomPane chatroomPane = null;
                    try {
                        client.join("#" + login);

                        chatroomPane = new ChatroomPane(client, login);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    JFrame frame2 = new JFrame("Message: " + login);

                    frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame2.setSize(500,500);
                    frame2.getContentPane().add(chatroomPane, BorderLayout.CENTER);
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
        chatroomName.setText("");
        client.join("#" + name);
        //client.restJoin("#" + name);
        online("#" + name);


    }

    public void setVisibleParentFrame() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.setVisible(false);
    }

    public void dispose() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.dispose();
    }

    public static void main(String[] args) throws IOException {
        ClientMain client = new ClientMain("localhost", 1234);

        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,600);
        //frame.pack();

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
        if (login.substring(0,1).equals("#")) {
            chatroomListModel.addElement(login.substring(1));
        } else {
            userListModel.addElement(login);
        }
    }

    @Override
    public void offline(String login) {
        if (login.substring(0,1).equals("#")) {
            chatroomListModel.removeElement(login.substring(1));
        } else {
            userListModel.removeElement(login);
        }
    }
}
