import javafx.scene.shape.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class GameBoardFrame extends JFrame {

    private final static Color bgcolor = new Color(0, 41, 58);
    private final static Color linecolor = new Color(170, 132, 57);

    private ArrayList<TerritoryPolygon> polygonList = new ArrayList<>();
    private ArrayList<Line> lineList = new ArrayList<>();
    private String CurrentAction = "";

    public JPanel mainPanel;

    public GameBoardFrame() {
        super("All Those Territories - © Langeder, Mauracher 2016");
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
                g2.setStroke(new BasicStroke(5.5f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                g2.setColor(linecolor);
                for (Line line : lineList) {
                    g2.drawLine((int) line.getStartX(), (int) line.getStartY(), (int) line.getEndX(), (int) line.getEndY());
                }

                //DRAW NOT HIGHLIGHTED SECTIONS
                for (TerritoryPolygon pol : polygonList) {
                    Territory current = GameBoard.territories.get(pol.getName());
                    if (current == null || current.getIsHovered()) continue;

                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    g2.setColor(Color.DARK_GRAY);
                    g2.drawPolygon(pol);
                    g2.setColor(current.getColor());
                    g2.fillPolygon(pol);

                    g2.setColor(Color.GREEN);
                    g2.drawString(String.valueOf(current.getArmy()), current.getCapital().x, current.getCapital().y);
                }

                //DRAW HIGHLIGHTED SECTIONS
                for (TerritoryPolygon pol : polygonList) {
                    Territory current = GameBoard.territories.get(pol.getName());
                    if (current == null || !current.getIsHovered()) continue;

                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    g2.setColor(Color.ORANGE);
                    g2.drawPolygon(pol);
                    g2.setColor(current.getColor());
                    g2.fillPolygon(pol);

                    g2.setColor(Color.GREEN);
                    g2.drawString(String.valueOf(current.getArmy()), current.getCapital().x, current.getCapital().y);
                }

                g2.drawString(getCurrentAction(), 625, 610);
            }
        };
        mainPanel.setBackground(bgcolor);
        mainPanel.setSize(1250, 650);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public void drawNew() {
        SwingUtilities.invokeLater(() -> mainPanel.repaint());
    }

    public void addPolygons(ArrayList<ArrayList<Point>> points, String name) {
        for (ArrayList<Point> pointList : points) {
            addPolygon(pointList, name);
        }
    }

    private void addPolygon(ArrayList<Point> points, String name) {
        TerritoryPolygon poly = new TerritoryPolygon(name);
        for (Point p : points) {
            poly.addPoint((int) (p.getX()), (int) p.getY());
        }
        polygonList.add(poly);
    }

    public void addLine(Point from, Point to) {
        lineList.add(new Line(from.x, from.y, to.x, to.y));
    }

    public String getClickedTerritory(int x, int y) {
        for (TerritoryPolygon pol : polygonList)
            if (pol.contains(x, y)) {
                drawNew();
                return pol.getName();
            }

        return null;
    }

    public String getCurrentAction() {
        return CurrentAction;
    }

    public void setCurrentAction(String value) {
        CurrentAction = value;
    }
}

class TerritoryPolygon extends Polygon {

    private String name;

    public TerritoryPolygon(String name) {
        super();
        this.name = name;
    }

    public String getName() { return name; }
}