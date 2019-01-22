package de.uniks.liverisk.logic;

import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {
    static final int MAXUNITS = 16;
    static final int TIME_PER_ROUND = 10000;

    static List<NonPlayerCharacter> npcs = new ArrayList<>();
    @Deprecated
    static public Game init(Player... players) {
        if(players.length < 2) {
            return null;
        }
        Game game = new Game();
        game.withPlayers((Object[]) players);
        for(Player p : players) {
            int i = 5;
            while (i != 0) {
                p.withUnits(new Unit());
                i--;
            }

        }

        Platform p1 = new Platform().setXPos(50).setYPos(200).setCapacity(5)
                .setGame(game);
        Platform p2 = new Platform().setXPos(650).setYPos(200).setCapacity(5)
                .setGame(game);
        Platform p3 = new Platform().setXPos(300).setYPos(100).setCapacity(3)
                .setGame(game);
        Platform p4 = new Platform().setXPos(400).setYPos(300).setCapacity(3)
                .setGame(game);
        p1.withNeighbors(p3, p4);
        p3.withNeighbors(p4);
        p2.withNeighbors(p3, p4);

        for(Player p : players) {
            for(Platform plat : game.getPlatforms()) {
                if(plat.getPlayer() == null) {
                    plat.setPlayer(p).withUnits(p.getUnits().get(0));
                    break;
                }
            }
        }
        game.setCurrentPlayer(players[0]);
        return game;
    }

    /**
     * Creates the game with the given players and platforms
     * @param players
     * @param platforms
     * @param gameName
     */
    static public Game init(List<Player> players, Player human, List<Platform> platforms, int startUnitsCount, String gameName
            , boolean randomStartPlat) {
        Game game = new Game();
        if(players.size() < 2) {
            Logger.getGlobal().log(Level.WARNING, "At least 2 players needed!");
            return null;
        }
        if(startUnitsCount < 1) {
            Logger.getGlobal().log(Level.WARNING, "At least 1 start-unit needed!");
            return null;
        }
        for(Platform p : platforms) {
            if(p.getCapacity() < 1) {
                Logger.getGlobal().log(Level.WARNING, "Every Platform needs at least capacity = 1");
                return null;
            }
        }
        for(Player p : players) {
            if(p != human) {
                npcs.add(new NonPlayerCharacter(p));
            }

            if(randomStartPlat) {
                for(Platform plat : platforms) {
                    if(plat.getPlayer() == null) {
                        plat.setPlayer(p);
                        break;
                    }
                }
            }
            if(p.getPlatforms().size() != 1) {
                Logger.getGlobal().log(Level.WARNING, "Every player needs exactly one start-platform");
                return null;
            }
            //Create start-units
            Platform startPlatform = p.getPlatforms().get(0);
            if(!startPlatform.getUnits().isEmpty() || !p.getUnits().isEmpty()) {
                Logger.getGlobal().log(Level.WARNING, "There are already units on start-platform or in player-reserve!");
                return null;
            }
            if(startPlatform.getCapacity() < startUnitsCount) {
                Logger.getGlobal().log(Level.WARNING, "Not enough capacity for start-units!");
                return null;
            }
            int i = startUnitsCount;
            while(i>0) {
                new Unit().setPlatform(startPlatform);
                i--;
            }
        }
        game.withPlayers(players).withPlatforms(platforms);
        game.setCurrentPlayer(players.get(0)).setName(gameName);

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                gameLoop(game);
            }
        }, 0, TIME_PER_ROUND, TimeUnit.MILLISECONDS);

        return game;
    }

    static private void gameLoop(Game game) {
        for(NonPlayerCharacter npc : npcs) {
            npc.reinforce();
        }

        for(Player p : game.getPlayers()) {
            for(int i = 0; i != p.getPlatforms().size() && p.getUnits().size() != MAXUNITS; i++) {
                p.withUnits(new Unit());
            }
        }

        for(NonPlayerCharacter npc : npcs) {
            npc.attack();
        }
    }

    static public void move(Platform source, Platform destination) {
        if(source == null || destination == null) {
            Logger.getGlobal().log(Level.WARNING, "source or destination is null!");
            return;
        }
        else if(source == destination) {
            Logger.getGlobal().log(Level.WARNING, "source and destination platform are the same!");
            return;
        }
        else if(source.getNeighbors().contains(destination) == false) {
            Logger.getGlobal().log(Level.WARNING, "The platforms are not connected!");
            return;
        }
        else if(destination.getPlayer() != source.getPlayer() && destination.getUnits().size() != 0) {
            Logger.getGlobal().log(Level.WARNING, "This platform has enemy units on it: Can't move!");
            return;
        }
        else if(destination.getCapacity() - destination.getUnits().size() == 0) {
            Logger.getGlobal().log(Level.WARNING, "No more capacity on this platform!");
            return;
        }
        else if(source.getUnits().size() == 0) {
            Logger.getGlobal().log(Level.WARNING, "No Unit on source platform!");
            return;
        }


        if(destination.getUnits().size() == 0) {
            destination.setPlayer(source.getPlayer());
        }
        source.getUnits().get(0).setPlatform(destination);
    }

    static public void reenforce(Platform platform) {
        if(platform == null) {
            Logger.getGlobal().log(Level.WARNING, "platform is null!");
            return;
        }
        else if(platform.getPlayer() == null) {
            Logger.getGlobal().log(Level.WARNING, "No player owns this platform!");
            return;
        }
        else if(platform.getCapacity() == platform.getUnits().size()) {
            Logger.getGlobal().log(Level.WARNING, "No more space on this platform");
            return;
        }
        for(Unit u : platform.getPlayer().getUnits()) {
            if(u.getPlatform() == null) {
                u.setPlatform(platform);
                u.setPlayer(null);
                return;
            }
        }
    }

    static public void attack(Platform source, Platform destination) {
        if(source == null || destination == null) {
            Logger.getGlobal().log(Level.WARNING, "source or destination is null!");
            return;
        }
        else if(source == destination) {
            Logger.getGlobal().log(Level.WARNING, "source and destination platform are the same!");
            return;
        }
        else if(source.getNeighbors().contains(destination) == false) {
            Logger.getGlobal().log(Level.WARNING, "The platforms are not connected!");
            return;
        }
        else if(destination.getPlayer() == source.getPlayer()) {
            Logger.getGlobal().log(Level.WARNING, "Player attacks himself!");
            return;
        }
        else if(source.getUnits().size() == 0) {
            Logger.getGlobal().log(Level.WARNING, "No Unit on source platform!");
            return;
        }

        while(destination.getUnits().size() != 0 && source.getUnits().size() != 1) {
            destination.getUnits().get(0).removeYou();
            source.getUnits().get(0).removeYou();
        }
        if(destination.getUnits().size() == 0) {
            destination.setPlayer(null);
            while(source.getUnits().size() > 1) {
                destination.setPlayer(source.getPlayer());
                source.getUnits().get(0).setPlatform(destination);
            }
        }
    }

    static public List<Platform> createSimpleMap() {
        Platform p1 = new Platform().setXPos(50).setYPos(200).setCapacity(5);
        Platform p2 = new Platform().setXPos(650).setYPos(200).setCapacity(5);
        Platform p3 = new Platform().setXPos(300).setYPos(100).setCapacity(3);
        Platform p4 = new Platform().setXPos(400).setYPos(300).setCapacity(3);
        p1.withNeighbors(p3, p4);
        p3.withNeighbors(p4);
        p2.withNeighbors(p3, p4);
        ArrayList<Platform> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        return list;
    }
}
