import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class MapLoader {

    private HashMap<String, Territory> territories = new HashMap<>();
    private HashMap<String, Continent> continents = new HashMap<>();

    public MapLoader(String mapFile) {
        readFile(mapFile);
    }

    public HashMap<String, Territory> getTerritories() {
        return territories;
    }

    public HashMap<String, Continent> getContinents() {
        return continents;
    }

    private void readFile(String mapFile) {
        try {
            FileReader in = new FileReader("maps/" + mapFile);
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                readMapLine(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void readMapLine(String line) {
        //Split by whitespace, but only first appearance
        String[] stringData = line.split(" ", 2);

        switch (stringData[0]) {
            case "patch-of":
                addPatch(stringData[1]);
                break;
            case "capital-of":
                addCapital(stringData[1]);
                break;
            case "neighbors-of":
                addNeighbors(stringData[1]);
                break;
            case "continent":
                addContinent(stringData[1]);
            default:
                break;
        }
    }

    public void addPatch(String patchData) {
        String terrName = getAffectedRegion(patchData);
        ArrayList<Point> coordinates = getCoordinates(patchData);

        Territory terr;

        //Territory already exists
        if(territories.containsKey(terrName)) {
            terr = territories.get(terrName);
            addPatches(terr, coordinates);

        } else {
            terr = new Territory();
            addPatches(terr, coordinates);
            territories.put(terrName, terr);
        }
    }

    public void addPatches(Territory terr, ArrayList<Point> coordinates) {
        for(Point p : coordinates) {
            terr.addPatch(p);
        }
    }

    public void addCapital(String capitalData) {
        String terrName = getAffectedRegion(capitalData);
        ArrayList<Point> coordinates = getCoordinates(capitalData);

        Territory terr;

        //Territory already exists
        if(territories.containsKey(terrName)) {
            terr = territories.get(terrName);
            terr.setCapital(coordinates.get(0));

        } else {
            terr = new Territory();
            terr.setCapital(coordinates.get(0));
            territories.put(terrName, terr);
        }
    }

    public void addNeighbors(String neighborData) {
        //TODO
    }

    public void addContinent(String continentData) {
        //TODO
        String continentName = getAffectedRegion(continentData);
        int unitBoost = getContinentUnitBoost(continentData);
        String[] territories = getTerritoriesFromContinent(continentData);
        continents.put(continentName, new Continent(territories, unitBoost));
    }

    public int getContinentUnitBoost(String line) {
        String[] stringData = line.split(" ");
        for(int i = 0; i < stringData.length; i++) {
            if(isNumber(stringData[i])) {
                return Integer.parseInt(stringData[i]);
            }
        }
        return 0;
    }

    public String[] getTerritoriesFromContinent(String line) {
        ArrayList<String> territories = new ArrayList<>();
        String[] stringData = line.split(": ");
        return stringData[1].split(" - ");
    }

    //Gets region to which attributes are assigned to
    //Region begins after first space of a line and ends at either " :" or a number
    public String getAffectedRegion(String line) {
        //Check for words with spaces(no numbers or special characters allowed)
        Matcher match = Pattern.compile("[a-zA-Z ]+").matcher(line);
        while (match.find()) {
            //return first result
            return match.group();
        }
        throw new IllegalArgumentException();
    }

    public ArrayList<Point> getCoordinates(String line) {
        String[] stringData = line.split(" ");

        ArrayList<Point> coordinates = new ArrayList<>();

        for(int i = 0; i < stringData.length; i++) {
            if(isNumber(stringData[i]) && isNumber(stringData[i+1])) {
                coordinates.add(new Point(Integer.parseInt(stringData[i]),
                                Integer.parseInt(stringData[i+1])));
                i++; //jump over next index
            }
        }
        return coordinates;
    }

    public static boolean isNumber(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}