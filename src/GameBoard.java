import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoard {
    private GameBoardFrame boardFrame;

    //GAMEDATA
    public static List<Territory> territories;
    public static List<Continent> continents;

    private Phase currentPhase;
    private Continent lastPickedContinent;
    private Territory lastPickedTerritory;
    private Player curPlayer = Player.Human;
    private int curUnits = 0;

    public GameBoard(String MAP) {
        MapLoader loader = new MapLoader(MAP);

        territories = loader.getTerritories();
        continents = loader.getContinents();

        currentPhase = Phase.Setup;

        boardFrame = new GameBoardFrame();
        addLinesToFrame();
        boardFrame.showFrame();
        addFrameListener();

        //autoFill(); //DEBUG!!!
    }

    private void addLinesToFrame() {
        for (Territory current : territories) {
            for (Territory neighbor : current.getNeighbors()) {
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

    private void addFrameListener() {
        boardFrame.mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Territory item = boardFrame.getClickedTerritory(e.getX(), e.getY());
                    if (item == null || curPlayer != Player.Human) return;


                }
                else if (SwingUtilities.isLeftMouseButton(e)) {
                    Territory item = boardFrame.getClickedTerritory(e.getX(), e.getY());
                    if (item == null || curPlayer != Player.Human) return;

                    if (currentPhase == Phase.Setup && item.getBelongsTo() == Player.None) {
                        item.setArmy(1);
                        item.setBelongsTo(Player.Human);

                        lastPickedContinent = item.getContinent();
                        lastPickedTerritory = item;

                        boardFrame.setCurrentAction("You picked: " + item.getName());
                        curPlayer = Player.Bot;

                        checkIfSetupEnded();

                        if(currentPhase == Phase.Setup)
                            pickNPCTerritory();
                    }
                    else if (currentPhase == Phase.Reinforce && curUnits > 0 && item.getBelongsTo() == curPlayer) {
                        curUnits--;
                        boardFrame.setUnitsLeft(curUnits);
                        item.setArmy(item.getArmy() + 1);
                        lastPickedTerritory = item;

                        if (curUnits <= 0) {
                            curPlayer = Player.Bot;
                            pickNPCReinforcements();
                            nextPhase();
                        }
                    }
                    else if (currentPhase == Phase.Conquest && item.getBelongsTo() == curPlayer && item.getArmy() > 1) {
                        deselectTerritory(lastPickedTerritory);
                        item.setIsSelected(true);
                        selectNeighbors(item);
                        lastPickedTerritory = item;
                    }
                    else if(currentPhase == Phase.Conquest && item.getBelongsTo() != curPlayer && lastPickedTerritory.getArmy() > 1) {
                        boardFrame.setCurrentPhase("Conquer - Attack & Move! \n" + lastPickedTerritory.Attack(item));
                        if (lastPickedTerritory.getArmy() == 1) {
                            deselectTerritory(item);
                            boardFrame.drawArrow = false;
                        }
                    }
                }
            }
        });

        boardFrame.mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                super.mouseMoved(e);

                territories.parallelStream().forEach(item -> item.setIsHovered(false));

                boardFrame.drawArrow = false;

                Territory item = boardFrame.getClickedTerritory(e.getX(), e.getY());
                if (item == null) return;

                if (currentPhase == Phase.Conquest) {
                    //Draw Arrow to attacking territory
                    if (item.getIsSelected() && item.getBelongsTo() != curPlayer) {
                        boardFrame.arrowFrom = lastPickedTerritory.getCapital();
                        boardFrame.arrowTo = item.getCapital();
                        boardFrame.drawArrow = true;
                    }
                }
                item.setIsHovered(true);
            }
        });

        boardFrame.nextRoundBtn.addActionListener(e -> {
            JButton b = (JButton) e.getSource();
            if(b.isEnabled() && currentPhase == Phase.Conquest && curPlayer == Player.Human) {
                curPlayer = Player.Bot;
                deselectTerritory(lastPickedTerritory);
                nextPhase();
            }
        });
    }

    private void selectNeighbors(Territory item) {
        if(item == null) return;
        item.getNeighbors().stream().filter(s -> curPlayer != s.getBelongsTo()).forEach(s -> s.setIsSelected(true));
    }

    private void deselectTerritory(Territory item) {
        if(item == null) return;
        item.setIsSelected(false);
        item.getNeighbors().stream().forEach(s -> s.setIsSelected(false));
    }

    private void pickNPCTerritory() {
        Territory item = getRandomFreeTerritoryFromContinent(lastPickedContinent);
        if (item == null)
            return;

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        item.setArmy(1);
                        item.setBelongsTo(Player.Bot);
                        //TODO: Override instead of append
                        boardFrame.setCurrentAction(boardFrame.getCurrentAction() + " - Opponent picked: " + item.getName());
                        curPlayer = Player.Human;
                        checkIfSetupEnded();
                    }
                },
                0
        );
    }

    private Territory getRandomFreeTerritory() {
        List<Territory> list = territories.parallelStream().filter(e -> e.getBelongsTo() == Player.None && e.getArmy() == 0).collect(Collectors.toList());

        if(list.size() == 0)
            return null;

        Random ran = new Random();
        return list.get(ran.nextInt(list.size()));
    }

    private Territory getRandomFreeTerritoryFromContinent(Continent c) {
        if(c == null)
            return getRandomFreeTerritory();

        List<Territory> list = c.getTerritories().parallelStream().filter(e -> e.getBelongsTo() == Player.None && e.getArmy() == 0).collect(Collectors.toList());
        if(list.size() == 0)
            return getRandomFreeTerritory();

        Random ran = new Random();
        return  list.get(ran.nextInt(list.size()));
    }

    private void checkIfSetupEnded() {
        for(Territory t : territories) {
            if(t.getArmy() == 0) {
                return;
            }
        }
        nextPhase();
    }

    private boolean checkIfGameEnded() {
        Player belongsTo = null;
        for(Territory t : territories) {
            if(belongsTo == null) {
                belongsTo = t.getBelongsTo();
            } else if(belongsTo != t.getBelongsTo()) {
                return false;
            }
        }
        return true;
    }

    private void nextPhase() {
        switch (currentPhase)
        {
            case Reinforce:
                currentPhase = Phase.Conquest;
                boardFrame.setCurrentPhase("Conquer - Attack & Move!");
                break;
            case Setup:
                curPlayer = Player.Human;
                currentPhase = Phase.Reinforce;
                boardFrame.setCurrentPhase("Conquest - Reinforce!");
                curUnits = calculateReinforcements();
                boardFrame.setUnitsLeft(curUnits);
                break;
        }
    }

    private int calculateReinforcements() {
        int countUnits = 0;
        int countTerritories = 0;
        boolean hasAll;
        for(Continent c : continents) {
            hasAll = true;
            for(Territory t : c.getTerritories()) {
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
        int units = calculateReinforcements();
        int restunits = units;
        double maxreinforce = 0;
        int count = 0;
        for(Territory t : territories) {
            if(t.getBelongsTo() == Player.Bot) {
                double index = t.getReinforceIndex(units);
                if(maxreinforce < index) {
                    maxreinforce = index;
                    count = 1;
                }
                else if(maxreinforce == index) {
                    count++;
                }
            }
        }

        for(Territory t : territories) {
            if(t.getBelongsTo() == Player.Bot) {
                double index = t.getReinforceIndex(units);
                if(maxreinforce == index) {
                    t.setArmy(t.getArmy() + (restunits / count));
                    restunits = restunits - (restunits / count);
                    count--;
                    if(count == 0)
                        t.setArmy(t.getArmy() + restunits);
                }
            }
        }

        curPlayer = Player.Human;
        nextPhase();
    }

    //DEBUG METHOD!!!
    private void autoFill() {
        Territory item = getRandomFreeTerritory();
        if(item != null) {
            item.setArmy(1);
            item.setBelongsTo(curPlayer);
            /*item.setIsSelected(hasselected);
            hasselected = !hasselected;*/
            boardFrame.setCurrentAction(boardFrame.getCurrentAction() + " - Opponent picked: " + item.getName());
            if(curPlayer == Player.Bot) {
                curPlayer = Player.Human;
            } else {
                curPlayer = Player.Bot;
            }
        }
        boolean isFinished = true;
        for(Territory t : territories) {
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