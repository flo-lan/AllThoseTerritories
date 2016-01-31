import java.util.ArrayList;

public class Continent {

    private ArrayList<String> territories;
    private int reinforcements;

    public Continent(ArrayList<String> territories, int reinforcements) {
        this.territories = territories;
        this.reinforcements = reinforcements;
    }

    public ArrayList<String> getTerritories() {
        return territories;
    }

    public int getReinforcements() {
        return reinforcements;
    }

    public boolean containsTerritory(String terr) {
        for(String s : territories) {
            if(s.equals(terr)) return true;
        }
        return false;
    }

    public String toString() {
        String s = "";
        s += "UnitBoost: " + reinforcements + "\n";
        for(String t : territories) {
            s += "Terr: " + t + "\n";
        }
        return s;
    }
}
