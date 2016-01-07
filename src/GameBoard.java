import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoard {

    private GameBoardFrame boardFrame;
    private HashMap<String, Territory> territories;
    private HashMap<String, Continent> continents;

    public GameBoard() {
        MapLoader loader = new MapLoader("world.map");

        territories = loader.getTerritories();
        continents = loader.getContinents();

        boardFrame = new GameBoardFrame();
        addPatchesToFrame();
        boardFrame.showFrame();

        addFrameListener();

        /*for(Map.Entry<String, Territory> entry : territories.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }
        for(Map.Entry<String, Continent> entry : continents.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }*/
    }

    private void addPatchesToFrame() {
        for(Map.Entry<String, Territory> entry: territories.entrySet()) {
            boardFrame.addPolygons(entry.getValue().getPatches(), entry.getKey());
        }
    }

    private void addFrameListener() {
        boardFrame.mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(boardFrame.getClickedTerritory(e.getX(), e.getY()));
            }
        });
    }
}
