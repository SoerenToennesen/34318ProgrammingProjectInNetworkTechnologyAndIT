import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
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
                } else if ("create".equalsIgnoreCase(cmd)) {
                    handleCreate(tokens);
                } /*else if ("othersjoin".equalsIgnoreCase(cmd)) {
                    handleOthersJoin(tokens);
                } */else if ("chatroomCreate".equalsIgnoreCase(cmd)) {
                    handleChatroomCreate(tokens);
                } else if ("fileTransfer".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 6);
                    handleFileTransfer(tokensMsg);
                }
                else {


                    //String msg = "Unknown command: " + cmd + "\r\n";
                    //outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleFileTransfer(String[] tokens) throws IOException {

        String sendTo = tokens[1];
        String fileName = tokens[2];
        String fileType = tokens[3];
        int fileSize = Integer.parseInt(tokens[4]);
        String fileInStringFormat = tokens[5];


        byte[] fileBytes = new byte[fileSize];
        for (int i = 0; i < fileSize; i++) {
            int x = Integer.parseInt(fileInStringFormat.substring(2 * i, 2 * (i + 1)), 16);
            fileBytes[i] = (byte) (x > 127 ? x - 256 : x);
        }

        FileUtils.writeByteArrayToFile(new File("ServerModule/Files/" + fileName), fileBytes);
        System.out.println("im here now3");
        System.out.println(sendTo);
        boolean isTopic = sendTo.charAt(0) == '#';
        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker: workerList) {
            if (isTopic) {
                System.out.println("im here now4");
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "fileTo " + sendTo + " " + fileName + " " + fileType + " " + fileInStringFormat + "\r\n";
                    worker.send(outMsg);
                }
            } else {
                System.out.println("im here now5");
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "fileTo " + sendTo + " " + fileName + " " + fileType + " " + fileInStringFormat + "\r\n";
                    worker.send(outMsg);
                }
            }

        }

    }

    private void handleChatroomCreate(String[] tokens) throws IOException {

        if (tokens.length > 0) {
            String name = tokens[1];

            ArrayList<String> chatroomsBuffer = new ArrayList<>();
            try {
                File file = new File("ServerModule/Logs/chatrooms.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    chatroomsBuffer.add(line);
                }
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



            if (!chatroomsBuffer.contains(name)) {

                File chatroomsFile = new File("ServerModule/Logs/chatrooms.txt");
                if (chatroomsFile.exists() && !chatroomsFile.isDirectory()) {
                    try(FileWriter fw = new FileWriter("ServerModule/Logs/chatrooms.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw))
                    {
                        out.println(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("ServerModule/Logs/chatrooms.txt"), StandardCharsets.UTF_8))) {
                        writer.write(name + "\r\n");
                    }
                }

                // I HAVE NO IDEA WHY, BUT WE NEED TO GIVE THE SERVER A "DUMMY"-MESSAGE
                // IN ORDER FOR IT TO READ IN THE CORRECT ORDER.
                // THIS IS INVOKED FROM THE READMESSAGE LOOP
                String msg3 = "Joined " + name + "\r\n";
                outputStream.write(msg3.getBytes());

                topicSet.add(name);

                // THIS IS INVOKED FROM THE CREATECHATROOM METHOD
                String msg = "Successful chatroom creation\r\n";
                outputStream.write(msg.getBytes());
            } else {
                String msg = "Unsuccessful chatroom creation\r\n";
                outputStream.write(msg.getBytes());
                System.err.println("Chatroom creation failed for " + name);
            }

            //String[] executeHandleJoin = {"join", name};

            /*
            List<ServerWorker> workerList = server.getWorkerList();
            // Send current user all other online logins
            for (ServerWorker worker: workerList) {
                if (worker.getLogin() != null) {
                    String msg2 = "Join " + worker.getLogin() + "\r\n";
                    send(msg2);
                }
            }
             */



            //handleJoin(executeHandleJoin);

            String msg = "Chatroom added to database: " + name + "\r\n";
            outputStream.write(msg.getBytes());
        }
    }

    private void handleCreate(String[] tokens) throws IOException {
        if (tokens.length > 1) {
            String user = tokens[1];
            String password = tokens[2];

            ArrayList<String> usersBuffer = new ArrayList<>();
            try {
                File file = new File("ServerModule/Logs/users.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    usersBuffer.add(line);
                }
                fr.close();
            } catch(IOException e) {
                e.printStackTrace();
            }

            ArrayList<String> passwordsBuffer = new ArrayList<>();
            try {
                File file = new File("ServerModule/Logs/passwords.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    passwordsBuffer.add(line);
                }
                fr.close();
            } catch(IOException e) {
                e.printStackTrace();
            }



            if (!usersBuffer.contains(user)) {
                //users.add(user);
                //passwords.add(password);

                File usersFile = new File("ServerModule/Logs/users.txt");
                File passwordsFile = new File("ServerModule/Logs/passwords.txt");
                if (usersFile.exists() && passwordsFile.exists() && !usersFile.isDirectory()) {
                    //System.out.println("notnotnot");
                    try(FileWriter fw = new FileWriter("ServerModule/Logs/users.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw))
                    {
                        out.println(user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try(FileWriter fw = new FileWriter("ServerModule/Logs/passwords.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw))
                    {
                        out.println(password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("ServerModule/Logs/users.txt"), StandardCharsets.UTF_8))) {
                        writer.write(user + "\r\n");
                    }

                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("ServerModule/Logs/passwords.txt"), StandardCharsets.UTF_8))) {
                        //System.out.println("yoyoyo");
                        writer.write(password + "\r\n");
                    }
                }

                String msg = "Successful registration\r\n";
                outputStream.write(msg.getBytes());
            } else {
                String msg = "Unsuccessful registration\r\n";
                outputStream.write(msg.getBytes());
                System.err.println("Registration failed for " + user);
            }


            System.out.println("does this get executed??");
            String msg = "User added to database: " + user + "\r\n";
            outputStream.write(msg.getBytes());
        }
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

    private void handleOthersJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            List<ServerWorker> workerList = server.getWorkerList();
            /*
            // Send current user all other online logins
            for (ServerWorker worker: workerList) {
                if (worker.getLogin() != null) {
                    if (!login.equals(worker.getLogin())) {


                        //handleJoin(new String[]{"join", topic});

                        String msg = "Joined chatroom: " + topic + "\r\n";
                        outputStream.write(msg.getBytes());
                        topicSet.add(topic);


                        String msg2 = "Online " + worker.getLogin() + "\r\n";
                        send(msg2);
                    }
                }
            }

             */
        }
    }

    // Format: "Message" "login" body...
    // Format: "Message" "#topic" body...

    private void handleMessage(String[] tokens) throws IOException {

        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();

        /*for (ServerWorker worker : workerList) {
            System.out.println(worker);
        }*/

        for (ServerWorker worker: workerList) {

            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "Message from " + login + " to " + sendTo + " " + body + "\r\n";
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
            boolean b = true;
            ArrayList<String> usersBuffer = new ArrayList<>();
            ArrayList<String> passwordsBuffer = new ArrayList<>();
            ArrayList<String> chatroomsBuffer = new ArrayList<>();


            try {
                File file = new File("ServerModule/Logs/users.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    usersBuffer.add(line);
                }
                fr.close();
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                File file = new File("ServerModule/Logs/passwords.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    passwordsBuffer.add(line);
                }
                fr.close();
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                File file = new File("ServerModule/Logs/chatrooms.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    chatroomsBuffer.add(line);
                }
                fr.close();
            } catch(IOException e) {
                e.printStackTrace();
            }






            for (int i = 0; i < usersBuffer.size(); i++) {
                if ((login.equals(usersBuffer.get(i)) && password.equals(passwordsBuffer.get(i)))
                    /*|| (login.equals("guest") && password.equals("guest"))
                        || (login.equals("jim") && password.equals("jim"))*/) {
                    b = false;
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

                    /*int j = 0;
                    for (ServerWorker worker: workerList) {
                        //System.out.println(j);
                        worker.topicSet.addAll(chatroomsBuffer);
                        j++;
                    }*/
                    for (ServerWorker worker: workerList) {
                        if (login.equals(worker.getLogin())) {
                            for (String s : chatroomsBuffer) {
                                worker.topicSet.add(s);
                                String msg4 = "AddToCollection " + s + "\r\n";
                                outputStream.write(msg4.getBytes());
                            }
                        }

                    }



                } /*else {
                    String msg = "Unsuccessful login\r\n";
                    outputStream.write(msg.getBytes());
                    System.err.println("Login failed for " + login);
                }*/
            }
            if (b) {

                /*for (String s : users) {
                    System.out.println(s);
                }
                for (String t : passwords) {
                    System.out.println(t);
                }*/

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
