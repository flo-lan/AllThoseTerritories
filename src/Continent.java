import java.util.List;

public class Continent {

    private List<Territory> _territories;
    private int _reinforcements;
    private String _name;

    public Continent(String Name, List<Territory> Territories, int Reinforcements) {
        _territories = Territories;
        _reinforcements = Reinforcements;
        _name = Name;
    }

    public List<Territory> getTerritories() {
        return _territories;
    }

    public int getReinforcements() {
        return _reinforcements;
    }

    public String toString() {
        return _name + ", UnitBoost: " + _reinforcements + "\n";
    }
}