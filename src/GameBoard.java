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

    public static List<Territory> territories;
    public static List<Continent> continents;

    private Phase currentPhase;
    private Continent lastPickedContinent;
    private Territory firstPickedTerritory;
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
                Territory item = boardFrame.getClickedTerritory(e.getX(), e.getY());
                if (item == null || curPlayer != Player.Human) return;

                Selector(item, SwingUtilities.isLeftMouseButton(e));
            }
        });

        boardFrame.mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                Territory item = boardFrame.getClickedTerritory(e.getX(), e.getY());
                MouseOverTerritory(item);
            }
        });

        boardFrame.nextRoundBtn.addActionListener(e -> {
            JButton b = (JButton) e.getSource();

            if(b.isEnabled() && currentPhase != Phase.Reinforce && currentPhase != Phase.Setup && curPlayer == Player.Human) {
                if(territoriesBelongTo(Player.Human, true)) {
                    boardFrame.setCurrentPhase("You won, congrats");
                    currentPhase = Phase.End;
                    return;
                }
                curPlayer = Player.Bot;
                attackAndMoveNPC();
                curPlayer = Player.Human;
                if(territoriesBelongTo(Player.Human, true)) {
                    boardFrame.setCurrentPhase("Bot won, bad luck");
                    currentPhase = Phase.End;
                    return;
                }
                if(lastPickedTerritory != null) {
                    lastPickedTerritory.setIsSelected(false);
                    lastPickedTerritory = null;
                }
                if(firstPickedTerritory != null) {
                    firstPickedTerritory.setIsSelected(false);
                    firstPickedTerritory = null;
                }

                currentPhase = Phase.Reinforce;

                curPlayer = Player.Human;
                boardFrame.setCurrentPhase("Conquest - Reinforce!");
                boardFrame.setUnitsLeft(curUnits = calculateReinforcements());
            }
        });
    }

    private boolean territoriesBelongTo(Player player, boolean all) {
        for(Territory item : territories) {
            if(item.getBelongsTo() == player && !all)
                return true;
            else if(item.getBelongsTo() != player && all)
                return false;
        }
        return all;
    }

    private void nextPhase() {
        switch (currentPhase) {
            case Reinforce:
                currentPhase = Phase.Conquest;

                boardFrame.setCurrentPhase("Conquer - Attack & Move!");
                break;
            case Setup:
                currentPhase = Phase.Reinforce;

                curPlayer = Player.Human;
                boardFrame.setCurrentPhase("Conquest - Reinforce!");
                boardFrame.setUnitsLeft(curUnits = calculateReinforcements());
                break;
            case End:
                currentPhase = Phase.Setup;
                boardFrame.setCurrentPhase("Setup");
                territories.parallelStream().forEach(item -> {
                    item.setArmy(0);
                    item.setIsSelected(false);
                    item.setBelongsTo(Player.None);
                });
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

    private void MouseOverTerritory(Territory item) {
        territories.parallelStream().forEach(i -> i.setIsHovered(false));
        if(item == null) return;

        if ((currentPhase == Phase.Attack || currentPhase == Phase.Move) && firstPickedTerritory != null && lastPickedTerritory == null) {
            if (firstPickedTerritory.getNeighbors().contains(item)) {
                boardFrame.arrowFrom = firstPickedTerritory.getCapital();
                boardFrame.arrowTo = item.getCapital();
                boardFrame.drawArrow = true;
            }
        } else if(currentPhase == Phase.Attacked && firstPickedTerritory != null && lastPickedTerritory != null) {
            boardFrame.arrowFrom = firstPickedTerritory.getCapital();
            boardFrame.arrowTo = lastPickedTerritory.getCapital();
            boardFrame.drawArrow = true;
        } else {
            boardFrame.drawArrow = false;
        }
        item.setIsHovered(true);
    }

    private void Selector(Territory item, boolean IsLeftMouseButton) {
        if(!IsLeftMouseButton) {
            if(currentPhase == Phase.Attacked && lastPickedTerritory != null && firstPickedTerritory != null) {
                lastPickedTerritory.setArmy(lastPickedTerritory.getArmy() + firstPickedTerritory.getArmy() - 1);
                firstPickedTerritory.setArmy(1);
            }
        } else {
            switch (currentPhase) {

                case Setup:
                    if (item.getBelongsTo() != Player.None) return;

                    item.setArmy(1);
                    item.setBelongsTo(Player.Human);

                    lastPickedContinent = item.getContinent();
                    boardFrame.setCurrentAction("You picked: " + item.getName());

                    curPlayer = Player.Bot;

                    if (!territoriesBelongTo(Player.None, false))
                        nextPhase();
                    else
                        pickNPCTerritory();

                    break;

                case Reinforce:
                    if (item.getBelongsTo() != Player.Human) return;

                    item.setArmy(item.getArmy() + 1);
                    boardFrame.setUnitsLeft(--curUnits);

                    if (curUnits == 0) {
                        curPlayer = Player.Bot;
                        pickNPCReinforcements();
                        nextPhase();
                    }

                    break;

                case Conquest:

                    if (firstPickedTerritory == null) {
                        if (item.getBelongsTo() != Player.Human || item.getArmy() <= 1) return;

                        firstPickedTerritory = item;
                        firstPickedTerritory.setIsSelected(true);
                        break;
                    }

                    if(firstPickedTerritory == item)
                        break;

                    if(!firstPickedTerritory.getNeighbors().contains(item)) {
                        if (item.getBelongsTo() == Player.Human) {
                            firstPickedTerritory.setIsSelected(false);
                            firstPickedTerritory = item;
                            firstPickedTerritory.setIsSelected(true);
                        }
                        break;
                    }

                    if (item.getBelongsTo() != Player.Human) {
                        currentPhase = Phase.Attack;
                        boardFrame.setCurrentPhase("Conquer - Attack!");
                        Selector(item, IsLeftMouseButton);
                    } else {
                        currentPhase = Phase.Move;
                        boardFrame.setCurrentPhase("Conquer - Move!");
                        Selector(item, IsLeftMouseButton);
                    }

                    break;

                case Attack:

                    if (firstPickedTerritory == null) {
                        if (item.getBelongsTo() != Player.Human || item.getArmy() <= 1) return;

                        firstPickedTerritory = item;
                        firstPickedTerritory.setIsSelected(true);
                        break;
                    }

                    if(firstPickedTerritory == item)
                        break;

                    if (item.getBelongsTo() != Player.Human && firstPickedTerritory.getNeighbors().contains(item)) {
                        currentPhase = Phase.Attacked;
                        lastPickedTerritory = item;

                        boardFrame.setCurrentAction(firstPickedTerritory.Attack(lastPickedTerritory));
                        firstPickedTerritory.setIsSelected(false);
                        if(firstPickedTerritory.getArmy() == 1) {
                            currentPhase = Phase.Attack;
                            firstPickedTerritory = null;
                            lastPickedTerritory = null;
                        }
                    }

                    break;

                case Attacked:

                    if (item != firstPickedTerritory && item.getBelongsTo() == Player.Human) {
                        currentPhase = Phase.Attack;

                        firstPickedTerritory = item;
                        item.setIsSelected(true);
                        break;
                    } else if (item.getBelongsTo() != Player.Human) {
                        currentPhase = Phase.Attack;
                        Selector(item, IsLeftMouseButton);
                    }

                    break;

                case Move:
                    if (item.getBelongsTo() != Player.Human) return;

                    if (firstPickedTerritory == null) {
                        firstPickedTerritory = item;
                        firstPickedTerritory.setIsSelected(true);
                        break;
                    }

                    if (firstPickedTerritory.getNeighbors().contains(item) && lastPickedTerritory == null) {
                        firstPickedTerritory.setIsSelected(false);
                        lastPickedTerritory = item;

                        if(firstPickedTerritory.getArmy() <= 1) break;

                        lastPickedTerritory.setArmy(lastPickedTerritory.getArmy() + 1);
                        firstPickedTerritory.setArmy(firstPickedTerritory.getArmy() - 1);
                        break;
                    }

                    if (firstPickedTerritory == item && lastPickedTerritory.getArmy() > 1) {
                        firstPickedTerritory.setArmy(firstPickedTerritory.getArmy() + 1);
                        lastPickedTerritory.setArmy(lastPickedTerritory.getArmy() - 1);
                    } else if (lastPickedTerritory == item && firstPickedTerritory.getArmy() > 1) {
                        lastPickedTerritory.setArmy(lastPickedTerritory.getArmy() + 1);
                        firstPickedTerritory.setArmy(firstPickedTerritory.getArmy() - 1);
                    }

                    break;

                case End:
                    nextPhase();
                    break;
            }
        }
    }

    private void attackAndMoveNPC() {
        List<Territory> lst = territories.parallelStream().filter(e -> e.getBelongsTo() == Player.Bot && e.getArmy() > 1).collect(Collectors.toList());
        for(Territory item : lst) {
            for(Territory nei : item.getNeighbors()) {
                if(nei.getArmy() < item.getArmy())
                    item.Attack(nei);
            }
        }
    }

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

                        if(!territoriesBelongTo(Player.None, false))
                            nextPhase();
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
}