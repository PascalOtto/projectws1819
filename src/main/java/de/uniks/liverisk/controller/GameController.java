package de.uniks.liverisk.controller;

import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {
    public Game init(Player... players) {
        if(players.length < 2) {
            return null;
        }
        Game game = new Game();
        game.withPlayers(players);
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

    public void move(Platform source, Platform destination) {
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

    public void reenforce(Platform platform) {
        if(platform == null) {
            Logger.getGlobal().log(Level.WARNING, "platform is null!");
            return;
        }
        else if(platform.getPlayer() == null) {
            Logger.getGlobal().log(Level.WARNING, "No player owns this platform!");
            return;
        }
        else if(platform.getCapacity() - platform.getUnits().size() == 0) {
            Logger.getGlobal().log(Level.WARNING, "This more space on this platform");
            return;
        }
        for(Unit u : platform.getPlayer().getUnits()) {
            if(u.getPlatform() == null) {
                u.setPlatform(platform);
            }
        }
    }

    public void attack(Platform source, Platform destination) {
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
            while(source.getUnits().size() > 1) {
                destination.setPlayer(source.getPlayer());
                source.getUnits().get(0).setPlatform(destination);
            }
        }
    }
}