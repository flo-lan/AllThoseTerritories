import javafx.scene.shape.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoardFrame extends JFrame {

    private final static Color bgcolor = new Color(0, 41, 58);
    private final static Color linecolor = new Color(170, 132, 57);

    private List<Line> lineList = new ArrayList<>();

    private String CurrentAction = "";
    private String currentPhase = "Setup";
    private String unitsLeft = "";

    public JPanel mainPanel;
    public JButton nextRoundBtn;

    public Point arrowFrom = new Point(0, 0);
    public Point arrowTo = new Point(0, 0);
    public boolean drawArrow = false;

    public GameBoardFrame() {
        super("All Those Territories - © Langeder, Mauracher 2016");

        for (Territory item : GameBoard.territories)
            item.AddPropertyChangeListener(evt -> SwingUtilities.invokeLater(() -> mainPanel.repaint()));

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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.setColor(linecolor);
                lineList.stream().forEach(e -> g2.drawLine((int) e.getStartX(), (int) e.getStartY(), (int) e.getEndX(), (int) e.getEndY()));

                List<Territory> NotSelectedTerritories = GameBoard.territories.parallelStream().filter(e -> e.getIsHovered() == false).collect(Collectors.toList());
                List<Territory> SelectedTerritories = GameBoard.territories.parallelStream().filter(e -> e.getIsHovered()).collect(Collectors.toList());

                //DRAW NOT HIGHLIGHTED SECTIONS
                NotSelectedTerritories.stream().forEach(e -> {
                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    e.getPatches().stream().forEach(pol -> {
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawPolygon(pol);
                        g2.setColor(e.getColor());
                        g2.fillPolygon(pol);
                    });
                    g2.setColor(Color.GREEN);
                    g2.drawString(String.valueOf(e.getArmy()), e.getCapital().x, e.getCapital().y);
                });

                //DRAW HIGHLIGHTED SECTIONS
                SelectedTerritories.stream().forEach(e -> {
                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    e.getPatches().stream().forEach(pol -> {
                        g2.setColor(Color.ORANGE);
                        g2.drawPolygon(pol);
                        g2.setColor(e.getColor());
                        g2.fillPolygon(pol);
                    });
                    g2.setColor(Color.GREEN);
                    g2.drawString(String.valueOf(e.getArmy()), e.getCapital().x, e.getCapital().y);
                });

                if (false) {
                    drawArrow(g2, arrowTo, arrowFrom, Color.black);
                }

                g2.setColor(Color.GREEN);
                g2.drawString(CurrentAction, 625, 610);
                g2.drawString("Current Phase: " + currentPhase, 5, 15);
                if (!unitsLeft.equals("") && !unitsLeft.equals("0"))
                    g2.drawString("Units left: " + unitsLeft, 5, 30);

            }
        };
        mainPanel.setBackground(bgcolor);
        mainPanel.setSize(1250, 650);
        mainPanel.setLayout(null);
        nextRoundBtn = new JButton("Next Round");
        nextRoundBtn.setBounds(1145, 615, 100, 30);
        nextRoundBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        mainPanel.add(nextRoundBtn);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    private void drawArrow(Graphics2D g2, Point to, Point from, Color color) {
        g2.setPaint(color);
        Line2D l = new Line2D.Double(from.getX(), from.getY(), to.getX(), to.getY());
        g2.draw(l);
        double phi = Math.toRadians(40);
        int barb = 20;

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        double x, y, rho = theta + phi;
        for (int j = 0; j < 2; j++) {
            x = to.x - barb * Math.cos(rho);
            y = to.y - barb * Math.sin(rho);
            g2.draw(new Line2D.Double(to.x, to.y, x, y));
            rho = theta - phi;
        }
    }

    public void addLine(Point from, Point to) {
        lineList.add(new Line(from.x, from.y, to.x, to.y));
    }

    public Territory getClickedTerritory(int x, int y) {
        for (Territory terr : GameBoard.territories) {
            for (Polygon pol : terr.getPatches()) {
                if (pol.contains(x, y)) return terr;
            }
        }
        return null;
    }

    public String getCurrentAction() {
        return CurrentAction;
    }

    public void setCurrentAction(String value) {
        CurrentAction = value;
        SwingUtilities.invokeLater(() -> mainPanel.repaint());
    }

    public void setCurrentPhase(String value) {
        currentPhase = value;
        SwingUtilities.invokeLater(() -> mainPanel.repaint());
    }

    public void setUnitsLeft(int value) {
        unitsLeft = String.valueOf(value);
        SwingUtilities.invokeLater(() -> mainPanel.repaint());
    }
}