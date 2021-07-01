package server;

import server.gameModel.ServerGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
//    private ServerGame serverGame;
    private static List<ServerHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8083, 2)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted from: " + socket.getInetAddress());
                ServerHandler serverHandler = new ServerHandler(socket);
                System.out.println("created");
                handlers.add(serverHandler);
                System.out.println(handlers.size());
                if (handlers.size()==2) {
                    new ServerGame(new ArrayList<>(handlers)).start();
                    System.out.println("ServerGame started");
                    handlers.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
