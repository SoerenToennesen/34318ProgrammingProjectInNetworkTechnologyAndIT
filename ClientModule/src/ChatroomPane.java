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
    private JButton uploadButton = new JButton("Send file");
    private JFileChooser fileChooser = new JFileChooser();

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

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        JPanel paneFiles = new JPanel();
        paneFiles.add(new JScrollPane(filesListUI));
        paneFiles.add(uploadButton);
        paneFiles.setPreferredSize(new Dimension(90,250));
        add(paneFiles, BorderLayout.EAST);

        filesListModel.addElement("testFile.png");
        filesListModel.addElement("testFile2.jpg");
        filesListModel.addElement("testFile3.gif");
        filesListModel.addElement("testFile4.txt");
        filesListModel.addElement("testFile5.jpeg");

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
