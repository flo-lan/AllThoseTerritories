import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoard {

    private HashMap<String, Territory> territories;
    private HashMap<String, Continent> continents;

    public GameBoard() {
        GameBoardFrame boardFrame = new GameBoardFrame();
        MapLoader loader = new MapLoader("three-continents.map");
        territories = loader.getTerritories();
        continents = loader.getContinents();

        for(Map.Entry<String, Territory> entry : territories.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }
        for(Map.Entry<String, Continent> entry : continents.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }
    }
}
