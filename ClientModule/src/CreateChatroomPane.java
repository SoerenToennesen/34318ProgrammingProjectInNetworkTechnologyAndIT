/*import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CreateChatroomPane extends JPanel {


    //JFrame frame;
    JPanel buttonPane, fieldsPanel;
    JLabel inputField;
    JTextField chatroomName;
    JButton ok, cancel;

    //JLabel inputField = new JLabel();
    //JTextField chatroomName = new JTextField();
    //JButton okButton = new JButton("OK");
    //JButton cancelButton = new JButton("Cancel");

    CreateChatroomPane() throws IOException {
        super("Create chatroom");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        buttonPane = new JPanel();
        fieldsPanel = new JPanel();
        inputField = new JLabel("Chatroom name:");
        chatroomName = new JTextField("");
        ok = new JButton("OK");
        cancel = new JButton("Cancel");

        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
        buttonPane.setLayout(new FlowLayout());

        fieldsPanel.add(inputField);
        fieldsPanel.add(chatroomName);

        buttonPane.add(ok);
        buttonPane.add(cancel);

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doCreateChatroom();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });




        add(fieldsPanel, BorderLayout.PAGE_START);
        add(buttonPane, BorderLayout.PAGE_END);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);


    }

    private void createChatroom() {
        String name = chatroomName.getText();
        try {



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

 */