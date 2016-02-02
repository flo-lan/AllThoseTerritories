import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Territory extends BaseClass {

    private final static Color botcolor = new Color(207, 83, 57);
    private final static Color bothighlightcolor = new Color(255, 44, 0);
    private final static Color mycolor = new Color(57, 83, 207);
    private final static Color myhighlightcolor = new Color(0, 44, 255);

    private String _name;
    private Continent _continent;
    private Point _capital;
    private Player _belongsTo = Player.None;
    private int _army = 0;

    private boolean _isSelected = false;
    private boolean _isHovered = false;

    private List<Territory> _neighbors;
    private List<Polygon> _patches;

    public Territory(String Name, Point Capital) {
        _name = Name;
        _capital = Capital;
        _neighbors = new ArrayList<>();
        _patches = new ArrayList<>();
    }
    public Territory(String Name) {
        _name = Name;
        _neighbors = new ArrayList<>();
        _patches = new ArrayList<>();
    }

    //METHODS
    public Color getColor() {
        if (_belongsTo == Player.None)
            return Color.GRAY;

        if (_belongsTo == Player.Bot) {
            if (_isSelected)
                return bothighlightcolor;
            return botcolor;
        }

        if (_isSelected)
            return myhighlightcolor;
        return mycolor;
    }

    public double getReinforceIndex(int Army) {
        double weakNei = 0;
        double strongNei = 0;

        for (Territory item : _neighbors) {
            if (item.getIsHovered() != _isHovered) {
                if (item.getArmy() < _army + Army)
                    weakNei++;
                else
                    strongNei++;
            }
        }

        weakNei = weakNei / (double) _neighbors.size();
        strongNei = strongNei / (double) _neighbors.size();

        if (weakNei + strongNei == 0) return 0;

        return weakNei + (strongNei * 0.5);
    }

    public String Attack(Territory item) {
        int defenseArmys = item.getArmy();
        int attackArmys = getArmy();
        if (defenseArmys > 2) {
            defenseArmys = 2;
            item.setArmy(defenseArmys - 2);
        } else {
            item.setArmy(0);
        }

        if (attackArmys > 4) {
            attackArmys = 3;
            setArmy(attackArmys - 3);
        } else {
            attackArmys--;
            setArmy(1);
        }

        while (attackArmys > 0 && defenseArmys > 0) {
            Random ran = new Random();
            List<Integer> attacknumbers = new ArrayList<>();
            List<Integer> defensenumbers = new ArrayList<>();

            for (int i = 1; i <= attackArmys; i++)
                attacknumbers.add(ran.nextInt(6) + 1);
            for (int i = 1; i <= defenseArmys; i++)
                defensenumbers.add(ran.nextInt(6) + 1);

            if (Collections.max(attacknumbers) > Collections.max(defensenumbers)) {
                defenseArmys--;
            } else {
                attackArmys--;
            }
        }

        if (attackArmys == 0) {
            item.setArmy(item.getArmy() + defenseArmys);
            return getName() + " hat " + item.getName() + " angegriffen und verloren";
        } else {
            if(item.getArmy() > 0) {
                setArmy(getArmy() + attackArmys);
            } else {
                item.setArmy(attackArmys);
                item.setBelongsTo(getBelongsTo());
                item.setIsSelected(false);
            }
            return getName() + " hat " + item.getName() + " angegriffen und gewonnen";
        }
    }

    //END METHODS

    //GET / SET
    public String getName() {
        return _name;
    }
    public int getArmy() {
        return _army;
    }

    public void setArmy(int get) {
        int oldvalue = _army;
        _army = get;
        RaisePropertyChanged("Army", get, oldvalue);
    }

    public Continent getContinent() {
        return _continent;
    }
    public void setContinent(Continent get) {
        Continent oldvalue = _continent;
        _continent = get;
        RaisePropertyChanged("Continent", get, oldvalue);
    }

    public void addPatch(Polygon p) {
        _patches.add(p);
    }
    public void addPatches(List<Polygon> p) {
        _patches.addAll(p.parallelStream().collect(Collectors.toList()));
    }
    public List<Polygon> getPatches() {
        return _patches;
    }

    public void addNeighbor(Territory item) {
        _neighbors.add(item);
    }
    public List<Territory> getNeighbors() {
        return _neighbors;
    }

    public void setCapital(Point get) {
        Point oldvalue = _capital;
        _capital = get;
        RaisePropertyChanged("Capital", get, oldvalue);

    }
    public Point getCapital() {
        return _capital;
    }

    public void setBelongsTo(Player get) {
        Player oldvalue = _belongsTo;
        _belongsTo = get;
        RaisePropertyChanged("BelongsTo", get, oldvalue);
    }
    public Player getBelongsTo() {
        return _belongsTo;
    }

    public void setIsHovered(boolean get) {
        boolean oldvalue = _isHovered;
        _isHovered = get;
        RaisePropertyChanged("IsHovered", get, oldvalue);
    }
    public boolean getIsHovered() {
        return _isHovered;
    }

    public void setIsSelected(boolean get) {
        boolean oldvalue = _isSelected;
        _isSelected = get;
        RaisePropertyChanged("IsSelected", get, oldvalue);
    }
    public boolean getIsSelected() {
        return _isSelected;
    }

    //END GET / SET

    public boolean equals(Object o) {
        if (o == null) return false;
        return ((Territory) o).getName() == _name;
    }

    public String toString() {
        return "Territory: " + _name;
    }
}