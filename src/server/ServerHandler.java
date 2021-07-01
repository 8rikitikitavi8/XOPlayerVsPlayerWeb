package server;
import java.io.*;
import java.net.Socket;

public class ServerHandler {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ServerHandler(Socket socket) throws IOException {
        this.socket = socket;
        System.out.println("start out");
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        System.out.println("start in");
        in =  new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        System.out.println("Object gone");
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

}
