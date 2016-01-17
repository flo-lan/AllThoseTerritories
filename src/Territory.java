import java.awt.*;
import java.util.ArrayList;

public class Territory {

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
