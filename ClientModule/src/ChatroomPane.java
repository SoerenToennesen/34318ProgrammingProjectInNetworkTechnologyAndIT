import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatroomPane extends JPanel implements ChatroomMessageListener {

    private final ClientMain client;
    private final String chatroom;
    //private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public ChatroomPane(ClientMain client, String chatroom) {
        this.client = client;
        this.chatroom = chatroom;
        //this.login = login;

        client.addChatroomMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> {
            try {
                String text = inputField.getText();
                client.message("#" + chatroom, text);
                listModel.addElement(""
                        + "You: " + text);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String currentTime = "<html><font color=\"green\">--- Sent at " + dateFormat.format(date) + "</font></html>";
                listModel.addElement(currentTime);

                inputField.setText("");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        });

    }

    @Override
    public void onChatroomMessage(String fromLogin, String messageBody) {
        if (!client.getMyName().equals(fromLogin)) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            //System.out.println(dateFormat.format(date));
            String line = "<html><font color=\"blue\">" + fromLogin + ": " + messageBody + "</font></html>";
            listModel.addElement(line);
            String currentTime = "<html><font color=\"green\">--- Sent at " + dateFormat.format(date) + "</font></html>";
            listModel.addElement(currentTime);
        }
    }
}
