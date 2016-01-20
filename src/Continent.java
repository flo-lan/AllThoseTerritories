import java.util.ArrayList;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class Continent {

    private ArrayList<String> territories;
    private int unitBoost;

    public Continent(ArrayList<String> territories, int unitBoost) {
        this.territories = territories;
        this.unitBoost = unitBoost;
    }

    public ArrayList<String> getTerritories() {
        return territories;
    }

    public boolean containsTerritory(String terr) {
        for(String s : territories) {
            if(s.equals(terr)) return true;
        }
        return false;
    }

    public String toString() {
        String s = "";
        s += "UnitBoost: " + unitBoost + "\n";
        for(String t : territories) {
            s += "Terr: " + t + "\n";
        }
        return s;
    }
}
