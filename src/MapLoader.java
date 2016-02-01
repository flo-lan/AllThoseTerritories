import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MapLoader {

    private List<Territory> territories = new ArrayList<>();
    private List<Continent> continents = new ArrayList<>();

    public MapLoader(String mapFile) {
        readFile(mapFile);
    }

    public List<Territory> getTerritories() {
        return territories;
    }

    public List<Continent> getContinents() {
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
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void readMapLine(String line) {
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
        List<Point> coordinates = getCoordinates(patchData);

        Polygon item = new Polygon();
        coordinates.forEach((e) -> item.addPoint(e.x, e.y));

        Optional<Territory> terr = territories.stream().filter(e -> e.getName().equals(terrName)).findFirst();

        if(terr.isPresent()) {
            terr.get().addPatch(item);
        } else {
            Territory te = new Territory(terrName);
            te.addPatch(item);
            territories.add(te);
        }
    }

    public void addCapital(String capitalData) {
        String terrName = getAffectedRegion(capitalData);
        List<Point> coordinates = getCoordinates(capitalData);

        Optional<Territory> terr = territories.stream().filter(e -> e.getName().equals(terrName)).findFirst();

        if(terr.isPresent()) {
            terr.get().setCapital(coordinates.get(0));
        } else {
            Territory item = new Territory(terrName, coordinates.get(0));
            territories.add(item);
        }
    }

    public void addNeighbors(String neighborData) {
        String terrName = getAffectedRegion(neighborData);
        List<String> territoryList = getTerritoryList(neighborData);

        Optional<Territory> terr = territories.stream().filter(e -> e.getName().equals(terrName)).findFirst();
        List<Territory> list = territories.stream().filter(e -> territoryList.remove(e.getName())).collect(Collectors.toList());

        if(terr.isPresent())
        {
            Territory currentterr = terr.get();

            list.forEach(item -> {
                currentterr.addNeighbor(item);
                item.addNeighbor(currentterr);
            });

            for(String item : territoryList)
            {
                Territory newitem = new Territory(item);
                territories.add(newitem);
                newitem.addNeighbor(currentterr);
                currentterr.addNeighbor(newitem);
            }
        }
    }

    public void addContinent(String continentData) {
        List<String> territoryList = getTerritoryList(continentData);
        List<Territory> list = territories.stream().filter(e -> territoryList.remove(e.getName())).collect(Collectors.toList());
        list.addAll(territoryList.stream().map(Territory::new).collect(Collectors.toList()));

        Continent con = new Continent(getAffectedRegion(continentData), list, getContinentReinforcements(continentData));
        list.stream().forEach(e -> e.setContinent(con));
        continents.add(con);
    }

    public int getContinentReinforcements(String line) {
        String[] stringData = line.split(" ");
        for(int i = 0; i < stringData.length; i++) {
            if(isNumber(stringData[i])) {
                return Integer.parseInt(stringData[i]);
            }
        }
        return 0;
    }

    public List<String> getTerritoryList(String line) {
        String[] stringData = line.split(" : ");
        stringData = stringData[1].split(" - ");
        return new ArrayList(Arrays.asList(stringData));
    }

    //Gets region to which attributes are assigned to
    //Region begins after first space of a line and ends at either " :" or a number
    public String getAffectedRegion(String line) {
        //Check for words with spaces(no numbers or special characters allowed)
        Matcher match = Pattern.compile("[a-zA-Z ]+[a-zA-Z]").matcher(line);
        while (match.find()) {
            //return first result
            return match.group();
        }
        throw new IllegalArgumentException();
    }

    public List<Point> getCoordinates(String line) {
        String[] stringData = line.split(" ");

        ArrayList<Point> coordinates = new ArrayList<>();

        for(int i = 0; i < stringData.length; i++)  {
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