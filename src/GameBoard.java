import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameBoard {
    private GameBoardFrame boardFrame;
    //Setup: Put units on territories
    //Reinforce: Place units at the start of a round
    //Conquest: Attack and Move units (after inital setup)
    private enum Phase {Setup, Reinforce,  Conquest};
    private Phase gamePhase;
    //Last continent, which player picked in setup phase, for NPC AI
    private Continent lastPickedContinent;
    private String lastPickedTerritory = "";
    public static HashMap<String, Territory> territories;
    public static HashMap<String, Continent> continents;
    public static enum Player {Human, Bot}
    private Player curPlayer;
    private int curUnits = 0;

    public GameBoard() {
        MapLoader loader = new MapLoader("world.map");

        territories = loader.getTerritories();
        continents = loader.getContinents();

        chooseRandomStartingPlayer();
        gamePhase = Phase.Setup;

        SwingUtilities.invokeLater(() -> {
            boardFrame = new GameBoardFrame();
            addLinesToFrame();
            addPatchesToFrame();
            boardFrame.showFrame();
            addFrameListener();
            if(curPlayer == Player.Bot) pickNPCTerritory();

            autoFill(); //DEBUG!!!
        });
    }

    private void addLinesToFrame() {
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            Territory current = entry.getValue();
            for (String nei : current.getNeighbors()) {
                Territory neighbor = territories.get(nei);

                if (Math.abs(current.getCapital().x - neighbor.getCapital().x) > 625) {
                    if (Math.abs(current.getCapital().y - neighbor.getCapital().y) > 325) {
                        if (current.getCapital().x > neighbor.getCapital().x) {
                            if (current.getCapital().y > neighbor.getCapital().y) {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x + 1250, neighbor.getCapital().y + 650));
                                boardFrame.addLine(new Point(current.getCapital().x - 1250, current.getCapital().y - 650), neighbor.getCapital());
                            } else {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x + 1250, neighbor.getCapital().y - 650));
                                boardFrame.addLine(new Point(current.getCapital().x - 1250, current.getCapital().y + 650), neighbor.getCapital());
                            }
                        } else {
                            if (current.getCapital().y > neighbor.getCapital().y) {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x - 1250, neighbor.getCapital().y + 650));
                                boardFrame.addLine(new Point(current.getCapital().x + 1250, current.getCapital().y - 650), neighbor.getCapital());
                            } else {
                                boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x - 1250, neighbor.getCapital().y - 650));
                                boardFrame.addLine(new Point(current.getCapital().x + 1250, current.getCapital().y + 650), neighbor.getCapital());
                            }
                        }
                    } else {
                        if (current.getCapital().x > neighbor.getCapital().x) {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x + 1250, neighbor.getCapital().y));
                            boardFrame.addLine(new Point(current.getCapital().x - 1250, current.getCapital().y), neighbor.getCapital());
                        } else {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x - 1250, neighbor.getCapital().y));
                            boardFrame.addLine(new Point(current.getCapital().x + 1250, current.getCapital().y), neighbor.getCapital());
                        }
                    }
                } else {
                    if (Math.abs(current.getCapital().y - neighbor.getCapital().y) > 325) {
                        if (current.getCapital().y > neighbor.getCapital().y) {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x, neighbor.getCapital().y + 650));
                            boardFrame.addLine(new Point(current.getCapital().x, current.getCapital().y - 650), neighbor.getCapital());
                        } else {
                            boardFrame.addLine(current.getCapital(), new Point(neighbor.getCapital().x, neighbor.getCapital().y - 650));
                            boardFrame.addLine(new Point(current.getCapital().x, current.getCapital().y + 650), neighbor.getCapital());
                        }
                    } else {
                        boardFrame.addLine(current.getCapital(), neighbor.getCapital());
                    }
                }
            }
        }
    }

    private void addPatchesToFrame() {
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            boardFrame.addPolygons(entry.getValue().getPatches(), entry.getKey());
        }
    }

    private boolean hasselected = false;

    private void addFrameListener() {
        boardFrame.mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e))
                {

                }
                else if(SwingUtilities.isLeftMouseButton(e)) {
                    String name = boardFrame.getClickedTerritory(e.getX(), e.getY());
                    if (name == null) return;

                    //GAMELOGIC

                    Territory item = territories.get(name);
                    if(curPlayer == Player.Human) {
                        if (gamePhase == Phase.Setup && item.getArmy() != 1) {
                            item.setArmy(1);
                            item.setBelongsTo(Player.Human);
                            /*item.setIsSelected(hasselected);
                            hasselected = !hasselected;*/
                            territories.put(name, item);
                            lastPickedContinent = getContinentFromTerritory(name);
                            lastPickedTerritory = name;
                            boardFrame.setCurrentAction("You picked: " + name);
                            curPlayer = Player.Bot;
                            pickNPCTerritory();
                            checkIfSetupEnded();
                        } else if (gamePhase == Phase.Reinforce && curUnits > 0 && item.getBelongsTo() == curPlayer) {
                            curUnits--;
                            boardFrame.setUnitsLeft(curUnits);
                            item.setArmy(item.getArmy() + 1);
                            lastPickedTerritory = name;
                            if (curUnits <= 0) {
                                startAttackPhase();
                            }
                        } else if (gamePhase == Phase.Conquest && item.getBelongsTo() == curPlayer) {
                           // hightlightNeighbors(item);
                            deselectTerritory(lastPickedTerritory);
                            item.setIsSelected(true);
                            selectNeighbors(item);
                            lastPickedTerritory = name;
                        }
                    }
                    //END GAMELOGIC

                    boardFrame.drawNew();
                }
            }
        });

        boardFrame.mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                String name = boardFrame.getClickedTerritory(e.getX(), e.getY());
                if (name == null) return;

                Territory item = territories.get(name);
                if (item == null || item.getIsSelected()) return;

                for (Map.Entry<String, Territory> cur : territories.entrySet())
                    cur.getValue().setIsHovered(false);

                item.setIsHovered(true);

                boardFrame.drawNew();
            }
        });

        boardFrame.nextRoundBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton b = (JButton) e.getSource();
                if(b.isEnabled()) {
                    curPlayer = Player.Bot;
                    deselectTerritory(lastPickedTerritory);
                    nextRound();
                }
            }
        });
    }

    private void selectNeighbors(Territory t) {
        Territory neighbor;
        for(String s : t.getNeighbors()) {
            neighbor = territories.get(s);
            if(neighbor.getBelongsTo() != curPlayer) {
                neighbor.setIsSelected(true);
            }
        }
    }

    private void deselectTerritory(String name) {
        Territory t = territories.get(name);
        t.setIsSelected(false);
        Territory neighbor;
        for(String s : t.getNeighbors()) {
            neighbor = territories.get(s);
            neighbor.setIsSelected(false);
        }
    }

    private void chooseRandomStartingPlayer() {
        Random r = new Random();
        int player = r.nextInt(2);
        if(player == 0) {
            curPlayer = Player.Human;
        } else {
            curPlayer = Player.Bot;
        }
    }

    private void pickNPCTerritory() {
        String name;
        if(lastPickedContinent == null) {
            //Bot starts to choose
            name = getRandomFreeTerritory();
        } else {
            name = lastPickedTerritory;
        }
        if(name != null) {
            Territory item = territories.get(name);
            item.setArmy(1);
            item.setBelongsTo(Player.Bot);
            /*item.setIsSelected(hasselected);
            hasselected = !hasselected;*/
            territories.put(name, item);
            //TODO: Override instead of append
            boardFrame.setCurrentAction(boardFrame.getCurrentAction() + " - Opponent picked: " + name);
            curPlayer = Player.Human;
        }
    }

    private Continent getContinentFromTerritory(String terrName) {
        for(Continent c : continents.values()) {
            if(c.containsTerritory(terrName)) {
                return c;
            }
        }
        return null;
    }

    private String getRandomFreeTerritory() {
        ArrayList<String> territoryNames = new ArrayList<>();
        for(String s : territories.keySet()) {
            territoryNames.add(s);
        }
        Collections.shuffle(territoryNames);
        for(String st : territoryNames) {
            Territory t = territories.get(st);
            if(t.getArmy() == 0) {
                return st;
            }
        }
        return null;
    }

    private String getRandomFreeTerritoryFromContinent(Continent c) {
        ArrayList<String> territoryList = c.getTerritories();
        Collections.shuffle(territoryList);
        for(String terrName : territoryList) {
            Territory item = territories.get(terrName);
            if(item.getArmy() == 0) {
                return terrName;
            }
        }
        //No free territory found in continent
        return getRandomFreeTerritory();
    }

    private void checkIfSetupEnded() {
        for(Territory t : territories.values()) {
            if(t.getArmy() == 0) {
                return;
            }
        }
        //No territory with 0 armies left
        //Switch Phase
        nextRound();
    }

    private void nextRound() {
        gamePhase = Phase.Reinforce;
        boardFrame.setCurrentPhase("Conquest - Reinforce!");
        curUnits = calculateReinforcements();
        boardFrame.setUnitsLeft(curUnits);
        if(curPlayer == Player.Bot) {
            pickNPCReinforcements();
        }
    }

    private void startAttackPhase() {
        gamePhase = Phase.Conquest;
        boardFrame.setCurrentPhase("Conquer - Attack & Move!");
    }

    private int calculateReinforcements() {
        int countUnits = 0;
        int countTerritories = 0;
        boolean hasAll;
        for(Continent c : continents.values()) {
            hasAll = true;
            for(String terrName : c.getTerritories()) {
                Territory t = territories.get(terrName);
                if(t.getBelongsTo() != curPlayer) {
                    hasAll = false;
                } else {
                    countTerritories++;
                }
            }
            if(hasAll) {
                countUnits += c.getReinforcements();
            }
        }
        return countUnits + (countTerritories / 3);
    }

    //TODO: add more intelligence
    private void pickNPCReinforcements() {
        for(Territory t : territories.values()) {
            if(t.getBelongsTo() == Player.Bot) {
                t.setArmy(t.getArmy() + curUnits);
                boardFrame.drawNew();
                curPlayer = Player.Human;
                nextRound();
                return;
            }
        }
    }

    //DEBUG METHOD!!!
    private void autoFill() {
        String name;

        name = getRandomFreeTerritory();
        if(name != null) {
            Territory item = territories.get(name);
            item.setArmy(1);
            item.setBelongsTo(curPlayer);
            /*item.setIsSelected(hasselected);
            hasselected = !hasselected;*/
            territories.put(name, item);
            boardFrame.setCurrentAction(boardFrame.getCurrentAction() + " - Opponent picked: " + name);
            if(curPlayer == Player.Bot) {
                curPlayer = Player.Human;
            } else {
                curPlayer = Player.Bot;
            }
        }
        boolean isFinished = true;
        for(Territory t : territories.values()) {
            if(t.getArmy() == 0) {
               isFinished = false;
            }
        }
        if(isFinished) {
            checkIfSetupEnded();
        } else {
            autoFill();
        }
    }
}