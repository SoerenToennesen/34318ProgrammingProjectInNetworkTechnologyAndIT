import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatroomPane extends JPanel implements ChatroomMessageListener, ChatroomFileListener {

    private final ClientMain client;
    private final String chatroom;
    //private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();
    private JButton uploadButton = new JButton("Attach file");
    private JFileChooser fileChooser = new JFileChooser();
    private JButton sendButton = new JButton("Send");

    private DefaultListModel<String> filesListModel = new DefaultListModel<>();
    private JList<String> filesListUI = new JList<>(filesListModel);


    public ChatroomPane(ClientMain client, String chatroom) {
        this.client = client;
        this.chatroom = chatroom;
        //this.login = login;


        client.addChatroomMessageListener(this);
        client.addChatroomFileListener(this);

        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Select a file to upload");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1,1,1,1);

        setLayout(new GridBagLayout());

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = GridBagConstraints.RELATIVE;
//        gbc.fill = GridBagConstraints.VERTICAL;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JScrollPane(messageList),gbc);
//        messageList.setPreferredSize(new Dimension(200,300));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 15;
        gbc.weightx = 10;
        gbc.gridwidth = 2;
//        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = -8;
        add(inputField, gbc);
        inputField.setText("Type a message...");
//        inputField.setPreferredSize(new Dimension(200,40));


        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridheight = GridBagConstraints.RELATIVE;
//        gbc.fill = GridBagConstraints.VERTICAL;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(filesListUI), gbc);
//        filesListUI.setPreferredSize(new Dimension(80,120));

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weighty = -8;
        gbc.ipady = 10;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
//        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1,1,3,1);
        add(uploadButton, gbc);
//        uploadButton.setPreferredSize(new Dimension(80,30));

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = -8;
        gbc.weightx = -10;
        gbc.ipady = 10;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(sendButton, gbc);








        filesListModel.addElement("testFile.png");
        filesListModel.addElement("testFile2.jpg");
        filesListModel.addElement("testFile3.gif");
        filesListModel.addElement("testFile4.txt");
        filesListModel.addElement("testFile5.jpeg");


        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText("");
            }
        });

        inputField.addActionListener(e -> {
            try {
                String text = inputField.getText();
                if (!text.equalsIgnoreCase("")) {
                    client.message("#" + chatroom, text);
                    listModel.addElement(""
                            + "You: " + text);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    String currentTime = "<html><font color=\"green\">--- Sent at " + dateFormat.format(date) + "</font></html>";
                    listModel.addElement(currentTime);

                    inputField.setText("");
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        });

        sendButton.addActionListener(e -> {
            try {
                String text = inputField.getText();
                if (!text.equalsIgnoreCase("")) {
                    client.message("#" + chatroom, text);
                    listModel.addElement(""
                            + "You: " + text);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    String currentTime = "<html><font color=\"green\">--- Sent at " + dateFormat.format(date) + "</font></html>";
                    listModel.addElement(currentTime);

                    inputField.setText("");
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        });

        uploadButton.addActionListener(e -> {
            try {

                fileChooser.showOpenDialog(this);
                String location = fileChooser.getSelectedFile().getAbsolutePath();
                String fileName = fileChooser.getSelectedFile().getName();
                //filesListModel.addElement(fileName);
                //System.out.println(location);

                client.fileTransfer("#" + chatroom, fileName, location);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        filesListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && filesListUI.getSelectedValue() != null) {
                    String fileName = filesListUI.getSelectedValue();
                    StringBuilder fileType = new StringBuilder();
                    for (int i = fileName.length() - 1; i >= 0; i--) {
                        if (fileName.charAt(i) == '.') {
                            fileType.reverse();
                            break;
                        }
                        fileType.append(fileName.charAt(i));
                    }
                    if (fileType.toString().equalsIgnoreCase("jpg")
                            || fileType.toString().equalsIgnoreCase("png")
                            || fileType.toString().equalsIgnoreCase("gif")
                            || (fileType.toString().equalsIgnoreCase("jpeg"))) {

                        ImageIcon imageIcon = new ImageIcon("ServerModule/Files/" + fileName);
                        Image image = imageIcon.getImage();
                        Icon icon = new ImageIcon(image);
                        JFrame frame2 = new JFrame("Image: " + fileName);
                        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame2.setLocationRelativeTo(null);
                        JLabel imageLabel = new JLabel(icon);
                        frame2.add(imageLabel);
                        //frame2.setIconImage(image);
                        frame2.setSize(300,300);
                        frame2.setVisible(true);
                    } else {
                        try {
                            File file = new File("ServerModule/Files/" + fileName);
                            if (!Desktop.isDesktopSupported()) {
                                System.out.println("Not supported");
                            }
                            Desktop desktop = Desktop.getDesktop();
                            if (file.exists())
                                desktop.open(file);
                        }
                        catch(Exception e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        filesListUI.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER && filesListUI.getSelectedValue() != null){
                    String fileName = filesListUI.getSelectedValue();
                    StringBuilder fileType = new StringBuilder();
                    for (int i = fileName.length() - 1; i >= 0; i--) {
                        if (fileName.charAt(i) == '.') {
                            fileType.reverse();
                            break;
                        }
                        fileType.append(fileName.charAt(i));
                    }
                    if (fileType.toString().equalsIgnoreCase("jpg")
                            || fileType.toString().equalsIgnoreCase("png")
                            || fileType.toString().equalsIgnoreCase("gif")
                            || (fileType.toString().equalsIgnoreCase("jpeg"))) {

                        ImageIcon imageIcon = new ImageIcon("ServerModule/Files/" + fileName);
                        Image image = imageIcon.getImage();
                        Icon icon = new ImageIcon(image);
                        JFrame frame2 = new JFrame("Image: " + fileName);
                        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame2.setLocationRelativeTo(null);
                        JLabel imageLabel = new JLabel(icon);
                        frame2.add(imageLabel);
                        //frame2.setIconImage(image);
                        frame2.setSize(300,300);
                        frame2.setVisible(true);
                    } else {
                        try {
                            File file = new File("ServerModule/Files/" + fileName);
                            if (!Desktop.isDesktopSupported()) {
                                System.out.println("Not supported");
                            }
                            Desktop desktop = Desktop.getDesktop();
                            if (file.exists())
                                desktop.open(file);
                        }
                        catch(Exception e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                }
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

    @Override
    public void onChatroomFile(String fileName) {
        filesListModel.addElement(fileName);
    }
}
