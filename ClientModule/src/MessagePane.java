import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements MessageListener {

    private final ClientMain client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public MessagePane(ClientMain client, String login) {
        this.client = client;
        this.login = login;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.message(login, text);
                    listModel.addElement("You: " + text);
                    inputField.setText("");
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String messageBody) {
        if (login.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + ": " + messageBody;
            //listModel is the conversation
            listModel.addElement(line);
        }


    }
}
