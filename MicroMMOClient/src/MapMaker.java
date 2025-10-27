import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//class for making maps in the server
public class MapMaker extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {
    public int mouseX, mouseY;
    public BufferedImage currentTile;

    public ArrayList<BufferedImage> tileSet = new ArrayList<BufferedImage>();
    File imageDirectory = new File("Images/Tiles");
    File[] imageFiles = imageDirectory.listFiles();

    public ArrayList<Tile> currentMap = new ArrayList<Tile>();
    int gridSize = 64;

    public MapMaker() {
        setSize(800, 600);
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addImages();
    }

    public void addImages(){
       for(File file : imageFiles){
           try {
               tileSet.add(ImageIO.read(new File("Images/Tiles/"+file.getName())));
           }
           catch(Exception e){
               System.out.println(e);
           }
       }


        currentTile = tileSet.get(0);

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int snapX = (e.getX() / gridSize) * gridSize;
        int snapY = (e.getY() / gridSize) * gridSize;

        currentMap.add(new Tile(snapX, snapY, currentTile));

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private int currentTileIndex = 0;
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(tileSet.isEmpty())
            return;

        currentTileIndex += e.getWheelRotation();

        if(currentTileIndex < 0)
            currentTileIndex = tileSet.size() - 1;
        else if(currentTileIndex >= tileSet.size())
            currentTileIndex = 0;

        currentTile = tileSet.get(currentTileIndex);
        repaint();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.RED);
        g.fillRect(mouseX, mouseY, 64, 64);

        if(!tileSet.isEmpty()) {
            g.drawImage(currentTile, mouseX, mouseY, 64, 64, this);
        }

        for(Tile tile : currentMap) {
            g.drawImage(tile.getImage(), tile.getX(), tile.getY(), 64, 64, this );
        }

    }
}
