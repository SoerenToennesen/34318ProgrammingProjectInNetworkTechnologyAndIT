import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class ChatroomPane extends JPanel implements ChatroomMessageListener {

    private final ClientMain client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public ChatroomPane(ClientMain client, String login) throws IOException {
        this.client = client;
        this.login = login;

        client.addChatroomMessageListener(this);
        client.join(login);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.message(login, text);
                    listModel.addElement(""
                            + "You: " + text);
                    inputField.setText("");
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onChatroomMessage(String fromLogin, String messageBody) {
        String line = fromLogin + ": " + messageBody;
        listModel.addElement(line);
    }
}
