import javax.swing.*;

public class Frame extends JFrame {



    public Frame() {
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);


        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }


}
