import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class UserListPane extends JPanel implements UserStatusListener, ChatroomStatusListener {


    private final ClientMain client;

    private JPanel pane1, pane2;

    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> userListUI = new JList<>(userListModel);


    private JLabel usersLabel = new JLabel("Online users:");
    private DefaultListModel<String> chatroomListModel = new DefaultListModel<>();
    private JList<String> chatroomListUI = new JList<>(chatroomListModel);

    private JLabel chatroomsLabel = new JLabel("Available chatrooms:");
    private JButton chatroomButton = new JButton("Create chatroom");
    private JTextField chatroomName = new JTextField("");
    private JButton logoutButton = new JButton("Logout");


    public UserListPane(ClientMain client) {
        this.client = client;
        this.client.addUserStatusListener(this);
        this.client.addChatroomStatusListener(this);

        pane1 = new JPanel();
        pane2 = new JPanel();


        setLayout(new GridLayout(0,2));
        pane1.add(usersLabel);
        pane1.add(new JScrollPane(userListUI));

        pane2.add(chatroomsLabel);
        pane2.add(new JScrollPane(chatroomListUI));
        pane2.add(chatroomName);

        chatroomName.setPreferredSize(new Dimension(200,30));
        chatroomListUI.setPreferredSize(new Dimension(200,5000));
        userListUI.setPreferredSize(new Dimension(200,5000));
        
        pane2.add(chatroomButton);
        pane1.add(logoutButton);
        logoutButton.setBackground(Color.BLACK);
        logoutButton.setForeground(Color.WHITE);



        add(pane1);
        //pane1.setPreferredSize(new Dimension(300,120));
        add(pane2);
        //pane2.setPreferredSize(new Dimension(300,240));

        //add(pane3);
        //pane2.setPreferredSize(new Dimension(300,240));


        /*for (int i = 0; i < chatroomListModel.getSize(); i++) {
            client.join(chatroomListModel.get(i));
        }

         */


        chatroomButton.addActionListener(e -> {
            doCreateChatroom();
            /*try {
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

             */
        });

        logoutButton.addActionListener(e -> {
            try {
                client.logoff();

                //dispose();
                //new ClientStart();


                setVisibleParentFrame();
                LoginPane loginPane = new LoginPane();
                JFrame frame = new JFrame("Login");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(loginPane, BorderLayout.CENTER);
                frame.setSize(300,200);
                //frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);


                } catch (IOException e2) {
                    e2.printStackTrace();
                }
        });


        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && userListUI.getSelectedValue() != null) {
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
                if (e.getClickCount() > 1 && chatroomListUI.getSelectedValue() != null) {



                    String chatroom = chatroomListUI.getSelectedValue();
                    //String login = userListUI.getSelectedValue();
                    ChatroomPane chatroomPane;
                    //client.join("#" + login);

                    chatroomPane = new ChatroomPane(client, chatroom);

                    JFrame frame2 = new JFrame("Message: " + chatroom);

                    frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame2.setSize(500,500);
                    assert chatroomPane != null;
                    frame2.getContentPane().add(chatroomPane, BorderLayout.CENTER);
                    frame2.setLocationRelativeTo(null);
                    frame2.setVisible(true);
                }
            }
        });

        chatroomName.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    doCreateChatroom();
                    /*
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

                     */
                }
            }
        });


        chatroomListUI.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER && chatroomListUI.getSelectedValue() != null){
                    String chatroom = chatroomListUI.getSelectedValue();
                    //String login = userListUI.getSelectedValue();
                    ChatroomPane chatroomPane;
                    //client.join("#" + login);

                    chatroomPane = new ChatroomPane(client, chatroom);

                    JFrame frame2 = new JFrame("Message: " + chatroom);

                    frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame2.setSize(500,500);
                    assert chatroomPane != null;
                    frame2.getContentPane().add(chatroomPane, BorderLayout.CENTER);
                    frame2.setLocationRelativeTo(null);
                    frame2.setVisible(true);
                }
            }
        });

        userListUI.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER && userListUI.getSelectedValue() != null){
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

    public void doCreateChatroom() {

        String name = chatroomName.getText();

        try {
            ArrayList<String> currentChatrooms = new ArrayList<>();
            for (int i = 0; i < chatroomListModel.getSize(); i++) {
                currentChatrooms.add(chatroomListModel.get(i));
            }
            if (name.equals("")) {
                JOptionPane.showMessageDialog(UserListPane.this, "Insert a chatroom name");
            } else if (currentChatrooms.contains(name)) {
                JOptionPane.showMessageDialog(UserListPane.this, "Chatroom already exists");
                chatroomName.setText("");
            } else if (name.contains(" ")) {
                JOptionPane.showMessageDialog(UserListPane.this, "Chatroom can't contain spaces");
                chatroomName.setText("");
            } else if (client.chatroomCreate("#" + name)) {

                //online("#" + name);
                chatroomName.setText("");

                /*
                client.join("#" + name);
                //client.restJoin("#" + name);
                online("#" + name);
                chatroomName.setText("");
                 */


            } else {
                JOptionPane.showMessageDialog(UserListPane.this, "Couldn't create chatroom");
                chatroomName.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*
        chatroomName.setText("");
        client.join("#" + name);
        //client.restJoin("#" + name);
        online("#" + name);
         */


    }




    public void setVisibleParentFrame() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.setVisible(false);
    }

    public void dispose() {
        JFrame parent = (JFrame) this.getTopLevelAncestor();
        parent.dispose();
    }

    public static void main(String[] args) {
        ClientMain client = new ClientMain("localhost", 1234);

        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,280);
        //frame.pack();

        frame.getContentPane().add(userListPane);
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
