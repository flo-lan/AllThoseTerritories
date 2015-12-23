import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class MapLoader {

    private HashMap<String, Territory> territories = new HashMap<>();

    public MapLoader(String mapFile) {
        readFile(mapFile);
    }

    public HashMap<String, Territory> getTerritories() {
        return territories;
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
            default:
                System.out.println("Corrupt data");
                break;
        }
    }

    public void addPatch(String patchData) {
        String terrName = getAffectedRegion(patchData);
        int[] coordinates = getCoordinates(patchData);

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

    public void addPatches(Territory terr, int[] coordinates) {
        for(int i = 0; i < coordinates.length; i += 2) {
            terr.addPatch(coordinates[i], coordinates[i+1]);
        }
    }

    public void addCapital(String capitalData) {
        String terrName = getAffectedRegion(capitalData);
        int[] coordinates = getCoordinates(capitalData);

        Territory terr;

        //Territory already exists
        if(territories.containsKey(terrName)) {
            terr = territories.get(terrName);
            terr.setCapital(coordinates[0], coordinates[1]);

        } else {
            terr = new Territory();
            terr.setCapital(coordinates[0], coordinates[1]);
            territories.put(terrName, terr);
        }
    }

    public void addNeighbors(String neighbourData) {
        //TODO
    }

    public String getAffectedRegion(String line) {
        String[] stringData = line.split(" : ");
        return stringData[0];
    }

    public int[] getCoordinates(String line) {
        String[] stringData = line.split(" : ");
        String[] stringCoordinates = stringData[1].split(" ");

        //convert StringArray to IntArray
        int[] coordinates = new int[stringCoordinates.length];
        for(int i = 0; i < coordinates.length; i++) {
            coordinates[i] = Integer.parseInt(stringCoordinates[i]);
        }
        return coordinates;
    }

    /* OLD METHOD BEFORE ":" sperations
    //Get Territory Name From File-line(can be separated by more than one space)
    public String getTerritoryFromData(String line) {
        String terr = "";
        String[] stringData = line.split(" ");
        //Regex for Name detection
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m;
        for(int i = 0; i < stringData.length; i++) {
            //First Element of line must be a territory
            //Add first one without space.
            if(i == 0) {
                terr += stringData[i];
            } else {
                m = p.matcher(stringData[i]);
                if(m.find()) {
                    //String contains part of territory name
                    terr += " " + stringData[i];
                } else {
                    //Territory name ended
                    break;
                }
            }
        }
        return terr;
    }*/
}
