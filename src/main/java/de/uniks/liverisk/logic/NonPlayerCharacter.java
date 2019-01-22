package de.uniks.liverisk.logic;

import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;

import java.util.ArrayList;
import java.util.List;

public class NonPlayerCharacter {
    Player player;

    public NonPlayerCharacter(Player player) {
        this.player = player;
    }

    public void reinforce() {
        List<Platform> savePlats = new ArrayList<Platform>();
        List<Platform> dangerPlats = new ArrayList<Platform>();

        for(Platform p : player.getPlatforms()) {
            boolean save = true;
            for(Platform neighbor : p.getNeighbors()) {
                if(neighbor.getPlayer() != player) {
                    save = false;
                }
            }
            if(save) {
                savePlats.add(p);
            }
            else {
                dangerPlats.add(p);
            }
        }

        // Reinforcing
        for(Platform p : dangerPlats) {
            while(p.getUnits().size() != p.getCapacity() && !player.getUnits().isEmpty())
                GameController.reenforce(p);
        }
        for(Platform p : savePlats) {
            while(p.getUnits().size() != p.getCapacity() && !player.getUnits().isEmpty())
                GameController.reenforce(p);
        }
    }

    public void attack() {
        List<Platform> savePlats = new ArrayList<Platform>();
        List<Platform> dangerPlats = new ArrayList<Platform>();

        for(Platform p : player.getPlatforms()) {
            boolean save = true;
            for(Platform neighbor : p.getNeighbors()) {
                if(neighbor.getPlayer() != player) {
                    save = false;
                }
            }
            if(save) {
                savePlats.add(p);
            }
            else {
                dangerPlats.add(p);
            }
        }
        //Attack
        for(Platform p : dangerPlats) {
            Platform attackThis = null;
            int enemies = Integer.MAX_VALUE;
            for(Platform neighbor : p.getNeighbors()) {
                if(neighbor.getPlayer() != player && neighbor.getUnits().size() < p.getUnits().size()
                        && enemies > neighbor.getUnits().size()) {
                    attackThis = neighbor;
                    enemies = neighbor.getUnits().size();
                }
            }
            if(attackThis != null)
                GameController.attack(p, attackThis);
        }
    }
}
