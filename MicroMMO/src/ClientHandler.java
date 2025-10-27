import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public int movementX, movementY;
    public UUID clientID;
    public String animState;
    public int animIndex;

    public boolean hasReceivedData = false;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try{
            dataIn = new DataInputStream(clientSocket.getInputStream());
            dataOut = new DataOutputStream(clientSocket.getOutputStream());

            while(true) {
                //Get Data In from Client
                clientID = UUID.fromString(dataIn.readUTF());
                movementX = dataIn.readInt();
                movementY = dataIn.readInt();
                animState = dataIn.readUTF();
                animIndex = dataIn.readInt();

                if(!hasReceivedData) {
                    hasReceivedData = true;
                }

                //send data out to clients
                for(ClientHandler otherClients: Server.clientsList) {
                    try {
                        if(otherClients != this) {
                            synchronized(otherClients.dataOut) {
                                otherClients.dataOut.writeUTF(clientID.toString());
                                otherClients.dataOut.writeInt(movementX);
                                otherClients.dataOut.writeInt(movementY);
                                otherClients.dataOut.writeUTF(animState);
                                otherClients.dataOut.writeInt(animIndex);
                                otherClients.dataOut.flush();
                            }
                        }
                    }catch(Exception e) {
                        System.out.println("Can't send data to other clients...");
                    }
                }

            }
        }
        catch(IOException e){
            System.out.println("Client " + clientSocket.getInetAddress() + " disconnected...");
            Server.clientsList.remove(this);
        }
        finally{
            try{
                clientSocket.close();
            }catch(IOException e){
                System.out.println("Client socket disconnection error...");
            }
        }
    }
}
