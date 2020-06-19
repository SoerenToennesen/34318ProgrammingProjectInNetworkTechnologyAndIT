import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String messageBody) {
                //System.out.println(fromLogin);
                System.out.println("Message from " + fromLogin + " " + messageBody);
            }
        });

        client.addChatroomMessageListener(new ChatroomMessageListener() {
            @Override
            public void onChatroomMessage(String fromLogin, String messageBody) {
                //System.out.println(fromLogin);
                System.out.println("Message from " + fromLogin + " " + messageBody);
            }
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
        String cmd = "join " + chatroomName;
        serverOut.write(cmd.getBytes());
    }

    public void restJoin(String chatroomName) throws IOException {
        String cmd = "othersjoin " + chatroomName;
        serverOut.write(cmd.getBytes());
    }

    public void leave(String chatroomName) throws IOException {
        String cmd = "leave " + chatroomName;
        serverOut.write(cmd.getBytes());
    }


    public boolean create(String user, String password) throws IOException {
        String cmd = "create " + user + " " + password + "\r\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response line: " + response);

        if ("Successful registration".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }
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
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("message".equalsIgnoreCase(cmd)) {

                        String check = tokens[3];
                        if (check.equals("to")) {
                            String[] tokensMsg = StringUtils.split(line, null, 6);
                            handleMessage(tokensMsg);
                        } else {
                            String[] tokensMsg = StringUtils.split(line, null, 4);
                            handleMessage(tokensMsg);
                        }



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


    // message jim hello whats up
    // [message, jim, hello, whats, up]
    // [message, jim, hello whats up]

    private void handleMessage(String[] tokensMsg) {
        String check = tokensMsg[3];
        if (check.equals("to")) {
            //String login = tokensMsg[2];
            String sendTo = tokensMsg[4];
            String messageBody = tokensMsg[5];
            for (MessageListener listener : messageListeners) {
                listener.onMessage(sendTo, messageBody);
            }
        } else {
            String login = tokensMsg[2];
            String messageBody = tokensMsg[3];
            for (MessageListener listener : messageListeners) {
                listener.onMessage(login, messageBody);
            }
        }



        /*String login = tokensMsg[2];
        String messageBody = tokensMsg[3];

        for (MessageListener listener : messageListeners) {
            listener.onMessage(login, messageBody);
        }
         */

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

    public boolean connect() throws IOException {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            //socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //socket.close();
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
