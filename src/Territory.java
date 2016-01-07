import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class Territory {

    private ArrayList<ArrayList<Point>> patches = new ArrayList<>();
    private ArrayList<String> neighbors = new ArrayList<>();
    private Point capital = new Point();

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

    public void setCapital(Point p) {
        capital = p;
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
