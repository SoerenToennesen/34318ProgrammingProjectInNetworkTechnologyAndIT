import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatusListener {


    private final ClientMain client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public UserListPane(ClientMain client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

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
    }

    public static void main(String[] args) throws IOException {
        ClientMain client = new ClientMain("localhost", 8818);

        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
