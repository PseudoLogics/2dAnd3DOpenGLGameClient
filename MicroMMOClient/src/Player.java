import java.awt.*;
import java.util.UUID;




public class Player{

    public int x, y;
    public boolean isLocalClient;
    public UUID playerID;
    public String currentAnimation;
    public int animIndex;

    public Player(int x, int y, boolean isLocalClient, UUID playerID, String currentAnimation, int animIndex) {
        this.x = x;
        this.y = y;
        this.isLocalClient = isLocalClient;
        this.playerID = playerID;
        this.currentAnimation = currentAnimation;
        this.animIndex = animIndex;
    }

}
