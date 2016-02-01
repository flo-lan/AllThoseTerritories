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

    public Continent(List<Territory> Territories, int Reinforcements) {
        _territories = Territories;
        _reinforcements = Reinforcements;
    }

    public List<Territory> getTerritories() {
        return _territories;
    }

    public int getReinforcements() {
        return _reinforcements;
    }

    public boolean containsTerritory(Territory terr) {
        return _territories.parallelStream().anyMatch(e -> e.equals(terr));
    }

    public boolean containsTerritory(String name) {
        return _territories.parallelStream().anyMatch(e -> e.getName() == name);
    }

    public String getName() { return _name; }

    public String toString() {
        return _name + ", UnitBoost: " + _reinforcements + "\n";
    }
}