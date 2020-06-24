import java.io.IOException;

public class CloseServer {


    private final ClientMain client;

    public CloseServer() throws IOException {
        this.client = new ClientMain("localhost",1234);
        client.closeServer();
    }


}
