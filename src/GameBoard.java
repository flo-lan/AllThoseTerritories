import java.util.HashMap;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoard {

    private HashMap<String, Territory> territories;

    public GameBoard() {
        GameBoardFrame boardFrame = new GameBoardFrame();
        MapLoader loader = new MapLoader("africa.map");
        territories = loader.getTerritories();
        for(String s : territories.keySet()) {
            System.out.println(s);
        }
        for(Territory t : territories.values()) {
            System.out.println(t.toString());
        }
    }
}
