import javafx.scene.shape.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoardFrame extends JFrame{

    private ArrayList<TerritoryPolygon> polygonList = new ArrayList<>();
    private ArrayList<Line> lineList = new ArrayList<>();
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

                g2.setColor(Color.GREEN);
                for(Line line : lineList)
                {
                    g2.drawLine((int)line.getStartX(), (int)line.getStartY(), (int)line.getEndX(), (int)line.getEndY());
                }

                for(TerritoryPolygon pol : polygonList) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawPolygon(pol);
                    g2.setColor(pol.getColor());
                    g2.fillPolygon(pol);
                }
            }
        };
        mainPanel.setBackground(Color.CYAN);
        mainPanel.setSize(1250, 650);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public void drawNew()
    {
        mainPanel.repaint();
    }

    public void cleanPolyList()
    {
        polygonList = new ArrayList<>();
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

    public void addLine(Point from, Point to)
    {
        lineList.add(new Line(from.x, from.y, to.x, to.y));
    }

    public String getClickedTerritory(int x, int y) {
        for(TerritoryPolygon pol : polygonList)
            if (pol.contains(x, y)) {
                drawNew();
                return pol.getName();
            }

        return null;
    }
}

class TerritoryPolygon extends Polygon {

    private String name;

    public TerritoryPolygon(String name) {
        super();
        this.name = name;
    }

    public Color getColor()
    {
        Territory item = GameBoard.territories.get(name);
        if(item == null || item.getArmy() == 0)
            return Color.GRAY;
        else if(item.getBelongsToBot())
            return Color.RED;
        return Color.BLUE;
    }

    public String getName() {
        return name;
    }
}