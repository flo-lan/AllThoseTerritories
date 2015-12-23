import java.util.HashMap;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoard {

    private HashMap<String, Territory> territories;

    public GameBoard() {
        GameBoardFrame boardFrame = new GameBoardFrame();
        MapLoader loader = new MapLoader("squares.map");
        territories = loader.getTerritories();
    }
}
