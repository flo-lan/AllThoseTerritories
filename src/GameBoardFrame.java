import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoardFrame extends JFrame{

    private ArrayList<Polygon> polygonList = new ArrayList<>();
    public JPanel mainPanel;

    public GameBoardFrame() {
        super("All Those Territories");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void showFrame() {
        setSize(1250, 650);
        setResizable(false);
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLUE);
                for(Polygon pol : polygonList) {
                    g.drawPolygon(pol);
                }
            }
        };
        mainPanel.setSize(1250, 650);
        mainPanel.setBackground(Color.CYAN);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public void addPolygons(ArrayList<ArrayList<Point>> points) {
        for(ArrayList<Point> pointList : points) {
            addPolygon(pointList);
        }
    }

    private void addPolygon(ArrayList<Point> points) {
        Polygon poly = new Polygon();
        for(Point p : points) {
            poly.addPoint((int)(p.getX()), (int) p.getY());
        }
        polygonList.add(poly);
    }
}