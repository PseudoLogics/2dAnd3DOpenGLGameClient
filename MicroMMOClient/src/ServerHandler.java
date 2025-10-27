import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ServerHandler{

    private static Socket socket;
    private static String ipAddress = "167.99.162.119";
    private static int port = 8080;
    private static DataOutputStream out;
    private static DataInputStream in;
    //for 2d instance
    public static Player localPlayer;

    public static UUID otherPlayersID;
    public static String otherPlayersAnimationState = "Forward";
    public static int otherPlayersAnimationIndex = 0;
    private static int otherPlayersX, otherPlayersY;

    public static ArrayList<Player> otherPlayersList = new ArrayList<>();


    public static void main(String[] args) {

            Frame frame = new Frame();

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
                                otherPlayersX = in.readInt();
                                otherPlayersY = in.readInt();
                                otherPlayersAnimationState = in.readUTF();
                                otherPlayersAnimationIndex = in.readInt();

                                boolean found = false;


                                for (Player p : otherPlayersList) {
                                    if (p.playerID.toString().equals(otherPlayersID.toString()) && !p.isLocalClient) {
                                        p.x = otherPlayersX;
                                        p.y = otherPlayersY;
                                        p.currentAnimation = otherPlayersAnimationState;
                                        p.animIndex = otherPlayersAnimationIndex;
                                        found = true;
                                        break;

                                    }
                                }


                                if (!found) {
                                    Player otherPlayer = new Player(otherPlayersX, otherPlayersY, false, otherPlayersID, otherPlayersAnimationState, otherPlayersAnimationIndex);
                                    otherPlayersList.add(otherPlayer);
                                    GamePanel.players.add(otherPlayer);
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

    }

    //For 2D data
    public static void sendMovement(int movementX, int movementY, int playerAnimIndex){
        try {
            if(out == null)
                return;

            out.writeUTF(localPlayer.playerID.toString());
            out.writeInt(movementX);
            out.writeInt(movementY);
            out.writeUTF(localPlayer.currentAnimation);
            out.writeInt(playerAnimIndex);
            out.flush();

        }catch(IOException e) {
            System.out.println("Could not send player movement to server: " + e);
        }
    }
}
