import java.util.HashMap;

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

        for(String s : territories.keySet()) {
            System.out.println(s);
        }
        for(Territory t : territories.values()) {
            System.out.println(t.toString());
        }
        for(String s : continents.keySet()) {
            System.out.println(s);
        }
        for(Continent c : continents.values()) {
            System.out.println(c.toString());
        }
    }
}
