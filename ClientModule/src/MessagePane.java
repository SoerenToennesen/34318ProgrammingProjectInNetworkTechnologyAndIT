import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

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
            }
        });

    }

    @Override
    public void onMessage(String fromLogin, String messageBody) {
        if (login.equalsIgnoreCase(fromLogin)) {

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
