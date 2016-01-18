import java.awt.*;
import java.util.ArrayList;

public class Territory {
    private final static Color botcolor = new Color(207, 83, 57);
    private final static Color bothighlightcolor = new Color(255, 44, 0);
    private final static Color mycolor = new Color(57, 83, 207);
    private final static Color myhighlightcolor = new Color(0, 44, 255);

    private ArrayList<ArrayList<Point>> patches = new ArrayList<>();
    private ArrayList<String> neighbors = new ArrayList<>();
    private Point capital = new Point();
    private boolean BelongsToBot = false;
    private boolean IsSelected = false;
    private boolean IsHovered = false;
    private int Army = 0;

    public Territory() {
    }

    public ArrayList<ArrayList<Point>> getPatches() {
        return patches;
    }

    public void addPatch(ArrayList<Point> p) {
        patches.add(p);
    }

    public void addNeighbor(String territory) {
        neighbors.add(territory);
    }

    public ArrayList<String> getNeighbors() { return neighbors; }

    public void setCapital(Point p) {
        capital = p;
    }

    public Point getCapital() { return capital; }

    public int getArmy()
    {
        return Army;
    }

    public void setArmy(int value) { Army = value; }

    public boolean getBelongsToBot() { return BelongsToBot; }

    public void setBelongsToBot(boolean value) { BelongsToBot = value; }

    public boolean getIsSelected() {return IsSelected;}

    public void setIsSelected(boolean value) {IsSelected = value;}

    public boolean getIsHovered() {return IsHovered;}

    public void setIsHovered(boolean value) {IsHovered = value;}

    public Color getColor() {
        if (Army == 0)
            return Color.GRAY;

        if (BelongsToBot) {
            if (IsSelected)
                return bothighlightcolor;
            return botcolor;
        }

        if (IsSelected)
            return myhighlightcolor;
        return mycolor;
    }

    public String toString() {
        String s = "";
        s += "Capital: " + capital.toString() + "\n";

        for(ArrayList<Point> pointList : patches) {
            for (Point p : pointList) {
                s += "Patch: " + p.toString() + "\n";
            }
        }

        for(String n : neighbors) {
            s += "Neighbor: " + n + "\n";
        }
        return s;
    }
}
