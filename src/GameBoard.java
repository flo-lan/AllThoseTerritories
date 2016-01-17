import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoard {
    private GameBoardFrame boardFrame;
    public static HashMap<String, Territory> territories;
    public static HashMap<String, Continent> continents;

    public GameBoard() {
        MapLoader loader = new MapLoader("world.map");

        territories = loader.getTerritories();
        continents = loader.getContinents();

        boardFrame = new GameBoardFrame();
        addLinesToFrame();
        addPatchesToFrame();
        boardFrame.showFrame();

        addFrameListener();
    }

    private void addLinesToFrame() {
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            Territory current = entry.getValue();
            for (String nei : current.getNeighbors()) {
                Territory neighbor = territories.get(nei);

                if (Math.abs(current.getCapital().x - neighbor.getCapital().x) > 625) {
                    if (Math.abs(current.getCapital().y - neighbor.getCapital().y) > 325) {
                        if (current.getCapital().x > neighbor.getCapital().x) {
                            if (current.getCapital().y > neighbor.getCapital().y) {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x + 1250, neighbor.getCapital().y + 650));
                                boardFrame.addLine(new Point(current.getCapital().x - 1250, current.getCapital().y - 650), neighbor.getCapital());
                            } else {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x + 1250, neighbor.getCapital().y - 650));
                                boardFrame.addLine(new Point(current.getCapital().x - 1250, current.getCapital().y + 650), neighbor.getCapital());
                            }
                        } else {
                            if (current.getCapital().y > neighbor.getCapital().y) {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x - 1250, neighbor.getCapital().y + 650));
                                boardFrame.addLine(new Point(current.getCapital().x + 1250, current.getCapital().y - 650), neighbor.getCapital());
                            } else {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x - 1250, neighbor.getCapital().y - 650));
                                boardFrame.addLine(new Point(current.getCapital().x + 1250, current.getCapital().y + 650), neighbor.getCapital());
                            }
                        }
                    } else {
                        if (current.getCapital().x > neighbor.getCapital().x) {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x + 1250, neighbor.getCapital().y));
                            boardFrame.addLine(new Point(current.getCapital().x - 1250, current.getCapital().y), neighbor.getCapital());
                        } else {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x - 1250, neighbor.getCapital().y));
                            boardFrame.addLine(new Point(current.getCapital().x + 1250, current.getCapital().y), neighbor.getCapital());
                        }
                    }
                } else {
                    if (Math.abs(current.getCapital().y - neighbor.getCapital().y) > 325) {
                        if (current.getCapital().y > neighbor.getCapital().y) {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x, neighbor.getCapital().y + 650));
                            boardFrame.addLine(new Point(current.getCapital().x, current.getCapital().y - 650), neighbor.getCapital());
                        } else {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x, neighbor.getCapital().y - 650));
                            boardFrame.addLine(new Point(current.getCapital().x, current.getCapital().y + 650), neighbor.getCapital());
                        }
                    } else {
                        boardFrame.addLine(current.getCapital(), neighbor.getCapital());
                    }
                }
            }
        }
    }

    private void addPatchesToFrame() {
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            boardFrame.addPolygons(entry.getValue().getPatches(), entry.getKey());
        }
    }

    private boolean hasselected = false;

    private void addFrameListener() {
        boardFrame.mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String name = boardFrame.getClickedTerritory(e.getX(), e.getY());
                if (name == null) return;

                //GAMELOGIC

                Territory item = territories.get(name);

                item.setArmy(23);
                item.setBelongsToBot(true);
                item.setIsSelected(hasselected);
                hasselected = !hasselected;

                territories.put(name, item);

                boardFrame.setCurrentAction(name + " clicked");

                //END GAMELOGIC

                boardFrame.drawNew();
            }
        });

        boardFrame.mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                for (Map.Entry<String, Territory> item : territories.entrySet())
                    item.getValue().setIsHovered(false);

                String name = boardFrame.getClickedTerritory(e.getX(), e.getY());
                if (name == null) return;

                Territory item = territories.get(name);
                if (item == null) return;

                item.setIsHovered(true);

                boardFrame.drawNew();
            }
        });
    }
}