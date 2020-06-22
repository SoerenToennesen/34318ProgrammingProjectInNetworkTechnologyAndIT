import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientMain {
    private final String serverName;
    private final int serverPort;

    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<ChatroomStatusListener> chatroomStatusListeners = new ArrayList<>();
    private ArrayList<ChatroomMessageListener> chatroomMessageListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

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
    public static void main(String[] args) throws IOException {
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

        //call-back when another user sends a message to this user
        client.addMessageListener((fromLogin, messageBody) -> {
            //System.out.println(fromLogin);
            System.out.println("Message from " + fromLogin + " " + messageBody);
        });

        client.addChatroomMessageListener((fromLogin, messageBody) -> {
            //System.out.println(fromLogin);
            System.out.println("Message from " + fromLogin + " " + messageBody);
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

    public void message(String sendTo, String messageBody) throws IOException {
        String cmd = "message " + sendTo + " " + messageBody + "\r\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {

        String cmd = "logoff\r\n ";
        serverOut.write(cmd.getBytes());

    }


    public void join(String chatroomName) throws IOException {
        String cmd = "join " + chatroomName + "\r\n";
        serverOut.write(cmd.getBytes());
    }

    public void restJoin(String chatroomName) throws IOException {
        String cmd = "othersjoin " + chatroomName + "\r\n";
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
        System.out.println("line over 3");
        System.out.println("Response line: " + response);
        System.out.println("line under 3");

        return "Successful registration".equalsIgnoreCase(response);
    }

    public boolean chatroomCreate(String chatroomName) throws IOException {

        String cmd = "chatroomCreate " + chatroomName + "\r\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("line over 2");
        System.out.println("Response line from chatroomCreate method: " + response);
        System.out.println("line under 2");

        //startMessageReader();
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

                System.out.println("line over");
                System.out.println("Response line from readMessageLoop: " + line);
                System.out.println("line under");

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




                    } else if ("jointherest".equalsIgnoreCase(cmd)) {
                        System.out.println("executing joined");
                        handleJoined(tokens);
                    } else if ("successful".equalsIgnoreCase(cmd)) {
                        System.out.println("executing successful");
                        handleJoined(tokens);
                    } else if ("joined".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("addtocollection".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
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

    private void handleJoined(String[] tokens) {



        String chatroomName = tokens[2];
        for (ChatroomStatusListener listener : chatroomStatusListeners) {
            listener.online(chatroomName);
        }
        String[] sendChatroomsOnline = {"Online", chatroomName};
        handleOnline(sendChatroomsOnline);
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
                //System.out.println("-----+------");
                //System.out.println(login);
                //System.out.println("+----------+");
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


    /*public String[] checkAllOnline() {



    }

     */
}
