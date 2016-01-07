import javax.swing.*;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoardFrame extends JFrame{

    public GameBoardFrame() {
        super("All Those Territories");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1250, 650);
        setResizable(false);
        setVisible(true);
    }
}
