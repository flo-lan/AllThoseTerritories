import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class Territory {

    private ArrayList<Point> patches = new ArrayList<>();
    private Point capital = new Point();

    public Territory() {

    }

    public void addPatch(int x, int y) {
        patches.add(new Point(x, y));
    }

    public void setCapital(int x, int y) {
        capital.setLocation(x, y);
    }

    public String toString() {
        String s = "";
        s += "Capital: " + capital.toString() + "\n";
        for(Point p : patches) {
            s += p.toString() + "\n";
        }
        return s;
    }
}
