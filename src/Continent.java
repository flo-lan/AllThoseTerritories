/**
 * Created by Florian Langeder on 23.12.15.
 */
public class Continent {

    private String[] territories;
    private int unitBoost;

    public Continent(String[] territories, int unitBoost) {
        this.territories = territories;
        this.unitBoost = unitBoost;
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
