import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientMain {
    private final String serverName;
    private final int serverPort;

    public Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<ChatroomStatusListener> chatroomStatusListeners = new ArrayList<>();
    private ArrayList<ChatroomMessageListener> chatroomMessageListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<FileListener> fileListeners = new ArrayList<>();
    private ArrayList<ChatroomFileListener> chatroomFileListeners = new ArrayList<>();

    private HashSet<String> topicSet = new HashSet<>();


    private String myName;
    public String getMyName() {
        return myName;
    }
    public void setMyName(String myName) {
        this.myName = myName;
    }


    public ClientMain(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    public void main(String[] args) throws IOException {
        ClientMain client = new ClientMain("localhost", 1234);
        //addUserStatusListener tells you the presence of a user when they go online/offline
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addChatroomStatusListener(new ChatroomStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });
        client.addMessageListener((fromLogin, messageBody) -> {
            System.out.println("Message from " + fromLogin + " " + messageBody);
        });

        client.addChatroomMessageListener((fromLogin, messageBody) -> {
            System.out.println("Message from " + fromLogin + " " + messageBody);
        });
        client.addFileListener((fileName) -> {
            System.out.println("fileTo " + fileName);
        });

        //After you connect you can login, and after you login you can send messages
        if (!client.connect()) {
            System.err.println("Connection failed right here...");
        } else {
            System.out.println("Connection successful right here...");
            if (client.login("guest", "guest")) {
                System.out.println("Login successful right here");
                client.message("jim", "Hello, how are you?");
            } else {
                System.err.println("Login failed");
            }
            //client.logoff();
        }
    }

    public void fileTransfer(String sendTo, String fileName, String location) throws IOException {
        //location = "C:\\Users\\bruger\\Documents\\Java Applications\\34318ProgrammingProjectInNetworkTechnologyAndIT\\Files\\testFile.png";
        StringBuilder fileType = new StringBuilder();
        for (int i = location.length() - 1; i >= 0; i--) {
            if (location.charAt(i) == '.') {
                fileType.reverse();
                break;
            }
            fileType.append(location.charAt(i));
        }

        FileInputStream fileInputStream = new FileInputStream(location);
        int fileSize = (int) fileInputStream.getChannel().size();
        byte[] bytes = new byte[fileSize];
        fileInputStream.read(bytes, 0, bytes.length);
        StringBuilder fileInStringFormat = new StringBuilder();
        for (byte b : bytes) {
            int x = (b < 0 ? b + 256 : b);
            String hexadecimalRepresentation = Integer.toHexString(x);
            if (hexadecimalRepresentation.length() < 2) {
                hexadecimalRepresentation = "0" + hexadecimalRepresentation;
            }
            fileInStringFormat.append(hexadecimalRepresentation);
        }

        String cmd = "fileTransfer " + sendTo + " " + fileName + " " + fileType + " " + fileSize + " " + fileInStringFormat + "\r\n";
        serverOut.write(cmd.getBytes());

    }

    public void message(String sendTo, String messageBody) throws IOException {
        String cmd = "message " + sendTo + " " + messageBody + "\r\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {

        String cmd = "logoff\r\n ";
        serverOut.write(cmd.getBytes());

    }

    public void leave(String chatroomName) throws IOException {
        String cmd = "leave " + chatroomName + "\r\n";
        serverOut.write(cmd.getBytes());
    }


    public boolean create(String user, String password) throws IOException {
        String cmd = "create " + user + " " + password + "\r\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response line: " + response);

        return "Successful registration".equalsIgnoreCase(response);
    }

    public boolean chatroomCreate(String chatroomName) throws IOException {

        String cmd = "chatroomCreate " + chatroomName + "\r\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response line from chatroomCreate method: " + response);
        return "Successful chatroom creation".equalsIgnoreCase(response);
    }





    public boolean login(String login, String password) throws IOException {

        String cmd = "login " + login + " " + password + "\r\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response line: " + response);

        if ("Login successful".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }

    }

    private void startMessageReader() {
        Thread t = new Thread(this::readMessageLoop);
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                System.out.println("Response line from readMessageLoop: " + line);

                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("message".equalsIgnoreCase(cmd)) {
                        if (tokens.length > 4) {
                            if (tokens[4].charAt(0) == '#') {
                                String[] tokensMsg = StringUtils.split(line, null, 6);
                                handleMessage(tokensMsg);
                            } else {
                                String[] tokensMsg = StringUtils.split(line, null, 4);
                                handleMessage(tokensMsg);
                            }
                        } else {
                            String[] tokensMsg = StringUtils.split(line, null, 4);
                            handleMessage(tokensMsg);
                        }
                    } else if ("joined".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("addtocollection".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("fileto".equalsIgnoreCase(cmd)) {
                        handleFileTransfer(tokens);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleFileTransfer(String[] tokens) {

        String login = tokens[1];
        String fileName = tokens[2];
        String fileType = tokens[3];
        String fileInStringFormat = tokens[4];
        System.out.println(fileName);
        if (login.charAt(0) == '#') {
            System.out.println("hello im here now");
            for (ChatroomFileListener listener : chatroomFileListeners) {
                listener.onChatroomFile(fileName);
            }
        } else {
            System.out.println("hello im here now2");
            for (FileListener listener : fileListeners) {
                listener.onFile(fileName);
            }
        }
    }

    private void handleMessage(String[] tokensMsg) {
        if (tokensMsg.length > 4) {
            if (tokensMsg[4].charAt(0) == '#') {
                String login = tokensMsg[2];
                String sendTo = tokensMsg[4];
                String messageBody = tokensMsg[5];
                for (ChatroomMessageListener listener : chatroomMessageListeners) {
                    listener.onChatroomMessage(login, messageBody);
                }
            } else {
                String login = tokensMsg[2];
                String messageBody = tokensMsg[3];
                for (MessageListener listener : messageListeners) {
                    listener.onMessage(login, messageBody);
                }
            }
        } else {
            String login = tokensMsg[2];
            String messageBody = tokensMsg[3];
            for (MessageListener listener : messageListeners) {
                listener.onMessage(login, messageBody);
            }
        }



    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    public void handleOnline(String[] tokens) {
        String login = tokens[1];
        if (login.substring(0,1).equals("#")) {
            for (ChatroomStatusListener listener : chatroomStatusListeners) {
                listener.online(login);
            }
        } else {
            for (UserStatusListener listener : userStatusListeners) {
                listener.online(login);
            }
        }

    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }


    public void addFileListener(FileListener listener) {
        fileListeners.add(listener);
    }
    public void addChatroomFileListener(ChatroomFileListener listener) {
        chatroomFileListeners.add(listener);
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }
    public void addChatroomStatusListener(ChatroomStatusListener listener) {
        chatroomStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addChatroomMessageListener(ChatroomMessageListener listener) {
        chatroomMessageListeners.add(listener);
    }
    public void removeChatroomMessageListener(ChatroomMessageListener listener) {
        chatroomMessageListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
}
