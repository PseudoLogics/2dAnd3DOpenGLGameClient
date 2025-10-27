import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ServerViewPanel extends JPanel {

    JFrame frame = new JFrame();
    ArrayList<ClientHandler> clientsList = new ArrayList<>();
    Timer timer = new Timer(30, this::updateClientList);

    public ServerViewPanel() {

            setBackground(Color.GREEN);
            frame.add(this);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            timer.start();

    }


        public void updateClientList(ActionEvent e) {
        synchronized (clientsList) {
            clientsList = new ArrayList<>(Server.clientsList);
        }
            repaint();
        }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

            for (ClientHandler c : clientsList) {
                if(c.hasReceivedData) //has the first packet of data been sent?
                    g.drawRect(c.movementX, c.movementY, 4,4);
            }

    }

}
