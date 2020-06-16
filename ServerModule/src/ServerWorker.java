import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(line)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else if ("message".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                }
                else {
                    String msg = "Unknown command: " + cmd + "\r\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) throws IOException {
        if (tokens.length > 1) {
            String topic = tokens[1];
            String msg = "Left chatroom: " + topic + "\r\n";
            outputStream.write(msg.getBytes());
            topicSet.remove(topic);
        }
    }

    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) throws IOException {

        if (tokens.length > 1) {
            String topic = tokens[1];
            String msg = "Joined chatroom: " + topic + "\r\n";
            outputStream.write(msg.getBytes());
            topicSet.add(topic);
        }

    }

    // Format: "Message" "login" body...
    // Format: "Message" "#topic" body...
    private void handleMessage(String[] tokens) throws IOException {

        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker: workerList) {

            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "Message from " + login + " to " + sendTo + ": " + body + "\r\n";
                    worker.send(outMsg);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "Message from " + login + " " + body + "\r\n";
                    worker.send(outMsg);
                }
            }
        }

    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        // Send other online users current user's status
        String onlineMsg = "Offline " + login + "\r\n";
        for (ServerWorker worker: workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();

    }


    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("guest") && password.equals("guest"))
                    || (login.equals("jim") && password.equals("jim"))) {
                String msg = "Login successful\r\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);


                List<ServerWorker> workerList = server.getWorkerList();

                // Send current user all other online logins
                for (ServerWorker worker: workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "Online " + worker.getLogin() + "\r\n";
                            send(msg2);
                        }
                    }
                }
                // Send other online users current user's status
                String onlineMsg = "Online " + login + "\r\n";
                for (ServerWorker worker: workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

            } else {
                String msg = "Unsuccessful login\r\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void send(String onlineMsg) throws IOException {
        if (login != null) {
            outputStream.write(onlineMsg.getBytes());
        }



    }

}
