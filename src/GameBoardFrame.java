import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoardFrame extends JFrame{

    private ArrayList<TerritoryPolygon> polygonList = new ArrayList<>();
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

                Graphics2D g2 = (Graphics2D) g; //Needed for Antialiasing
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke( 5.5f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                for(TerritoryPolygon pol : polygonList) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawPolygon(pol);
                    g2.setColor(Color.GRAY);
                    g2.fillPolygon(pol);
                }
            }
        };
        mainPanel.setSize(1250, 650);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public void addPolygons(ArrayList<ArrayList<Point>> points, String name) {
        for(ArrayList<Point> pointList : points) {
            addPolygon(pointList, name);
        }
    }

    private void addPolygon(ArrayList<Point> points, String name) {
        TerritoryPolygon poly = new TerritoryPolygon(name);
        for(Point p : points) {
            poly.addPoint((int)(p.getX()), (int) p.getY());
        }
        polygonList.add(poly);
    }

    public String getClickedTerritory(int x, int y) {
        String terrName = "";
        for(TerritoryPolygon pol : polygonList) {
           if(pol.contains(x, y)) {
               return pol.getName();
           }
        }

        return terrName;
    }
}

class TerritoryPolygon extends Polygon {

    private String name;

    public TerritoryPolygon(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
}