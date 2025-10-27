import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ServerHandlerThreeD{

    private static Socket socket;
    private static String ipAddress = "167.99.162.119";
    private static int port = 8080;
    private static DataOutputStream out;
    private static DataInputStream in;

    //for 3d instance
    public static TexturedModel localPlayer;

    public static UUID otherPlayersID;
    //public static String otherPlayersAnimationState = "Forward";
    //public static int otherPlayersAnimationIndex = 0;
    private static float otherPlayersX, otherPlayersY, otherPlayersZ;

    public static ArrayList<TexturedModel> otherPlayersList = new ArrayList<>();


    public static void main(String[] args) {

        ThreeDGamePanel threeDGamePanel = new ThreeDGamePanel();

        //localPlayer = GamePanel.thisPlayer;


        try {
            socket = new Socket(ipAddress, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());


            //Get data from the server
            Thread serverListener = new Thread(() -> {

                try {
                    while (true) {


                        otherPlayersID = UUID.fromString(in.readUTF());
                        otherPlayersX = in.readFloat();
                        otherPlayersY = in.readFloat();
                        otherPlayersZ = in.readFloat();

                        boolean found = false;


                        for (TexturedModel tm : otherPlayersList) {
                            if (tm.uuid.toString().equals(otherPlayersID.toString()) && !tm.isLocal) {
                                tm.x = otherPlayersX;
                                tm.y = otherPlayersY;
                                tm.z = otherPlayersZ;
                                found = true;
                                break;

                            }
                        }


                        if (!found) {
                            TexturedModel otherPlayer = new TexturedModel("Models/Zombie.obj","Models/Zombie.png", otherPlayersX, otherPlayersY, otherPlayersZ, false, otherPlayersID);
                            otherPlayersList.add(otherPlayer);
                            ThreeDGamePanel.modelList.add(otherPlayer);
                            System.out.println("added player " + otherPlayersID);
                        }

                    }
                } catch (IOException e) {
                    System.out.println("Could not read other player's movements: " + e);
                }
            });


            serverListener.start();



        }
        catch(IOException e){
            System.out.println("Could not connect to server. " + e.getMessage());
        }


        threeDGamePanel.run();

    }


    //For 3D data
    public static void sendMovement3D(float movementX, float movementY, float movementZ){
        try {
            if(out == null)
                return;

            out.writeUTF(localPlayer.uuid.toString());
            out.writeFloat(movementX);
            out.writeFloat(movementY);
            out.writeFloat(movementZ);
        } catch (IOException e) {
            System.out.println("Could not send player movement to server: " + e);
        }
    }
}
