import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.GraphicsEnvironment;

public class Server {
    private static final int SERVER_PORT = 8080;
    public static final ArrayList<ClientHandler> clientsList = new ArrayList<>();

    public static void main(String[] args) {

        if(!GraphicsEnvironment.isHeadless()) {
            ServerViewPanel serverViewPanel = new ServerViewPanel();
            serverViewPanel.setVisible(true);
        }

        try (
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("Server started on port:" + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(clientSocket);

                synchronized (clientsList) {
                    clientsList.add(handler);
                }
                new Thread(handler).start();
            }
        }
        catch (IOException e) {
            System.out.println("Server failed to listen on port: " + SERVER_PORT);
        }
    }

}



