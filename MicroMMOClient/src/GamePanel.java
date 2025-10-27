import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel implements KeyListener {

    public static CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
    private static boolean hasSpawnedLocalClient = false;
    public static Player localPlayer;
    public static int playerAnimIndex = 0;

    Timer t = new Timer(30, this::frameRate);

    Image background = Toolkit.getDefaultToolkit().getImage("Images/PlotOfLand.png");

    Image spriteForward = Toolkit.getDefaultToolkit().getImage("Images/Front/WalkFFirst.png");
    Image spriteBack = Toolkit.getDefaultToolkit().getImage("Images/Back/WalkBFirst.png");
    Image spriteLeft = Toolkit.getDefaultToolkit().getImage("Images/Left/WalkLFirst.png");
    Image spriteRight = Toolkit.getDefaultToolkit().getImage("Images/Right/WalkRFirst.png");


    Image spriteForwardWalk = Toolkit.getDefaultToolkit().getImage("Images/Front/WalkFLF.png");
    Image spriteBackWalk = Toolkit.getDefaultToolkit().getImage("Images/Back/WalkBLF.png");
    Image spriteLeftWalk = Toolkit.getDefaultToolkit().getImage("Images/Left/WalkLLF.png");
    Image spriteRightWalk = Toolkit.getDefaultToolkit().getImage("Images/Right/WalkRLF.png");


    Image spriteForwardWalk2 = Toolkit.getDefaultToolkit().getImage("Images/Front/WalkFSD.png");
    Image spriteBackWalk2 = Toolkit.getDefaultToolkit().getImage("Images/Back/WalkBSD.png");
    Image spriteLeftWalk2 = Toolkit.getDefaultToolkit().getImage("Images/Left/WalkLSD.png");
    Image spriteRightWalk2 = Toolkit.getDefaultToolkit().getImage("Images/Right/WalkRSD.png");


    Image spriteForwardWalk3 = Toolkit.getDefaultToolkit().getImage("Images/Front/WalkFRF.png");
    Image spriteBackWalk3 = Toolkit.getDefaultToolkit().getImage("Images/Back/WalkBRF.png");
    Image spriteLeftWalk3 = Toolkit.getDefaultToolkit().getImage("Images/Left/WalkLRF.png");
    Image spriteRightWalk3 = Toolkit.getDefaultToolkit().getImage("Images/Right/WalkRRF.png");


    Image spriteForwardWalkLast = Toolkit.getDefaultToolkit().getImage("Images/Front/WalkFLast.png");
    Image spriteBackWalkLast = Toolkit.getDefaultToolkit().getImage("Images/Back/WalkBLast.png");
    Image spriteLeftWalkLast = Toolkit.getDefaultToolkit().getImage("Images/Left/WalkLLast.png");
    Image spriteRightWalkLast = Toolkit.getDefaultToolkit().getImage("Images/Right/WalkRLast.png");


    ArrayList<Image> walkForwardAnimationList = new ArrayList<>();
    ArrayList<Image> walkBackwardAnimationList = new ArrayList<>();
    ArrayList<Image> walkLeftAnimationList = new ArrayList<>();
    ArrayList<Image> walkRightAnimationList = new ArrayList<>();


    static String selectedAnimation = "Forward";
    Image curPlayerImage = spriteForward;


    public GamePanel() {
        t.start();
        addKeyListener(this);

        loadImages();

        //spawn local player on screen / add to players list
        setBackground(Color.GREEN);
        setFocusable(true);
        setLayout(null);


        if(!hasSpawnedLocalClient) {
            localPlayer = new Player(10,10,true, UUID.randomUUID(), "Forward", 0);
            players.add(localPlayer);
            hasSpawnedLocalClient = true;

            ServerHandler.localPlayer = localPlayer;

        }

        MapMaker mp = new MapMaker();
        add(mp);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Player player : players) {

            if(player.isLocalClient){
                g.setColor(Color.blue);
                g.drawImage(curPlayerImage, player.x - 64, player.y - 64, 128, 128, this);
                g.drawRect(player.x, player.y, 4, 4);
                g.fillRect(player.x, player.y, 4, 4);
            }

            else {
                g.setColor(Color.red);
                Image otherPlayersImageToDraw = spriteForwardWalk;

                if(player.currentAnimation.equals("Forward")){
                    otherPlayersImageToDraw = walkForwardAnimationList.get(player.animIndex);
                }
                if(player.currentAnimation.equals("Back")){
                    otherPlayersImageToDraw = walkBackwardAnimationList.get(player.animIndex);
                }
                if(player.currentAnimation.equals("Left")){
                    otherPlayersImageToDraw = walkLeftAnimationList.get(player.animIndex);
                }
                if(player.currentAnimation.equals("Right")){
                    otherPlayersImageToDraw = walkRightAnimationList.get(player.animIndex);
                }

                g.drawImage(otherPlayersImageToDraw, player.x - 96, player.y - 96, 192, 192, this);
                g.drawRect(player.x, player.y, 4, 4);
                g.fillRect(player.x, player.y, 4, 4);
            }
        }



    }

    @Override
    public void keyTyped(KeyEvent e) {


    }

    int dirX = 0, dirY = 0;
    boolean upIsPressed = false, backIsPressed = false, leftIsPressed = false, rightIsPressed = false, upAndRightIsPressed = false;
    int playerSpeed = 2;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_S:
                upIsPressed = true;
                dirY = 2;
                selectedAnimation = "Forward";
                break;
            case KeyEvent.VK_W:
                backIsPressed = true;
                dirY = -2;
                selectedAnimation = "Back";
                break;
            case KeyEvent.VK_A:
                leftIsPressed = true;
                dirX = -2;
                selectedAnimation = "Left";
                break;
            case KeyEvent.VK_D:
                rightIsPressed = true;
                dirX = 2;
                selectedAnimation = "Right";
                break;
            case KeyEvent.VK_W + KeyEvent.VK_D:
                upAndRightIsPressed = true;
                selectedAnimation = "UpAndRight";
                dirY = 2;
                dirX = 2;
                break;

            default:
                dirX = 0;
                dirY = 0;
                break;

        }

    }


    @Override
    public void keyReleased(KeyEvent e) {
        dirX = 0;
        dirY = 0;

        switch(selectedAnimation) {
            case "Forward": curPlayerImage = spriteForward; upIsPressed = false; break;
            case "Back": curPlayerImage = spriteBack; backIsPressed = false; break;
            case "Left": curPlayerImage = spriteLeft; leftIsPressed = false; break;
            case "Right": curPlayerImage = spriteRight; rightIsPressed = false; break;

        }
    }

    public int bufferMax = 5;// Frame buffer for animation call
    public int timerTick = 0;

    public void frameRate(ActionEvent e){

        for(Player player : players) {
            if(player.isLocalClient)
            {
                player.currentAnimation = selectedAnimation;
                player.y += dirY;
                player.x += dirX;
                ServerHandler.sendMovement(player.x, player.y, playerAnimIndex);
            }

        }


            timerTick++;

        if((dirX != 0 || dirY != 0) && (timerTick >= bufferMax)) {
            changeFrame();
            timerTick = 0;
        }

        repaint();

    }


    public void changeFrame(){
        switch(selectedAnimation){
            case "Forward":
                if(walkForwardAnimationList.indexOf(curPlayerImage) < walkForwardAnimationList.size() - 1)
                {
                    curPlayerImage = walkForwardAnimationList.get(walkForwardAnimationList.indexOf(curPlayerImage) + 1);
                }
                else{
                    curPlayerImage = walkForwardAnimationList.get(0);
                }
                playerAnimIndex = walkForwardAnimationList.indexOf(curPlayerImage);
                break;
            case "Back":
                if(walkBackwardAnimationList.indexOf(curPlayerImage) < walkBackwardAnimationList.size() - 1)
                {
                    curPlayerImage = walkBackwardAnimationList.get(walkBackwardAnimationList.indexOf(curPlayerImage) + 1);
                }
                else{
                    curPlayerImage = walkBackwardAnimationList.get(0);
                }
                playerAnimIndex = walkBackwardAnimationList.indexOf(curPlayerImage);
                break;
            case "Left":
                if(walkLeftAnimationList.indexOf(curPlayerImage) < walkLeftAnimationList.size() - 1)
                {
                    curPlayerImage = walkLeftAnimationList.get(walkLeftAnimationList.indexOf(curPlayerImage) + 1);
                }
                else{
                    curPlayerImage = walkLeftAnimationList.get(0);
                }
                playerAnimIndex = walkLeftAnimationList.indexOf(curPlayerImage);
                break;
            case "Right":
                if(walkRightAnimationList.indexOf(curPlayerImage) < walkRightAnimationList.size() - 1)
                {
                    curPlayerImage = walkRightAnimationList.get(walkRightAnimationList.indexOf(curPlayerImage) + 1);
                }
                else{
                    curPlayerImage = walkRightAnimationList.get(0);
                }
                playerAnimIndex = walkRightAnimationList.indexOf(curPlayerImage);
                break;

        }
    }

    public void loadImages(){
        walkForwardAnimationList.add(spriteForward);
        walkForwardAnimationList.add(spriteForwardWalk);
        walkForwardAnimationList.add(spriteForwardWalk2);
        walkForwardAnimationList.add(spriteForwardWalk3);
        walkForwardAnimationList.add(spriteForwardWalkLast);

        walkBackwardAnimationList.add(spriteBack);
        walkBackwardAnimationList.add(spriteBackWalk);
        walkBackwardAnimationList.add(spriteBackWalk2);
        walkBackwardAnimationList.add(spriteBackWalk3);
        walkBackwardAnimationList.add(spriteBackWalkLast);

        walkLeftAnimationList.add(spriteLeft);
        walkLeftAnimationList.add(spriteLeftWalk);
        walkLeftAnimationList.add(spriteLeftWalk2);
        walkLeftAnimationList.add(spriteLeftWalk3);
        walkLeftAnimationList.add(spriteLeftWalkLast);

        walkRightAnimationList.add(spriteRight);
        walkRightAnimationList.add(spriteRightWalk);
        walkRightAnimationList.add(spriteRightWalk2);
        walkRightAnimationList.add(spriteRightWalk3);
        walkRightAnimationList.add(spriteRightWalkLast);


    }
}
