package de.uniks.liverisk.logic;

import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.RectBounds;
import de.uniks.liverisk.event.Client;
import de.uniks.liverisk.event.ServerGameController;
import de.uniks.liverisk.gui.PersistenceUtil;
import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;
import com.sun.javafx.geom.Point2D;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {
    static final int MAXUNITS = 16;
    private static int UPDATE_RATE = 50;

    static  Random ran = new Random();
    static List<NonPlayerCharacter> npcs = new ArrayList<>();
    static PersistenceUtil persistenceUtil = new PersistenceUtil();
    static ScheduledFuture schedule = null;
    // sets this in online mode! Actions will not be executed!
    public static Client client = null;

    static private void gameLoop(Game game) {
        if(game.getWinner() != null) {
            game.setTimeLeft(0);
            stopGameLoop();
            return;
        }
        if(game.getTimeLeft() <= 0) {

            for (NonPlayerCharacter npc : npcs) {
                npc.reinforce();
            }

            for (Player p : game.getPlayers()) {
                for (int i = 0; i != p.getPlatforms().size() && p.getUnits().size() != MAXUNITS; i++) {
                    p.withUnits(new Unit());
                }
            }

            for (NonPlayerCharacter npc : npcs) {
                npc.attack();
            }

            persistenceUtil.save(game);
            game.setTimeLeft(game.getTimePerRound());
        }
        else {
            game.setTimeLeft(game.getTimeLeft()-UPDATE_RATE);
        }
    }

    static public void stopGameLoop() {
        if(schedule != null) {
            schedule.cancel(false);
        }
        schedule = null;
    }

    static public void startGameloop(Game game, int update_rate) {
        UPDATE_RATE = update_rate;
        if(game.getIsRunning() == false) {
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            schedule = exec.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    gameLoop(game);
                }
            }, UPDATE_RATE, UPDATE_RATE, TimeUnit.MILLISECONDS);
            game.setIsRunning(true);
        }
    }

    static public void setNonPlayerCharacters(Game game, int humans) {
        for(int i = 0; i != game.getPlayers().size(); i++) {
            if (i >= humans) {
                npcs.add(new NonPlayerCharacter(game.getPlayers().get(i)));
            }
        }
    }

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
    static public Game init(List<Player> players, List<Platform> platforms, int startUnitsCount, String gameName
            , boolean randomStartPlat) {
        List<Platform> startPlats = new ArrayList<>(platforms);
        Game game = new Game();
        if(players.size() < 2) {
            Logger.getGlobal().log(Level.WARNING, "At least 2 players needed!");
            return null;
        }
        if(startUnitsCount < 1) {
            Logger.getGlobal().log(Level.WARNING, "At least 1 start-unit needed!");
            return null;
        }
        int id = 0;
        for(Platform p : platforms) {
            //give ids
            p.setId(id++);
            if(p.getCapacity() < 1) {
                Logger.getGlobal().log(Level.WARNING, "Every Platform needs at least capacity = 1");
                return null;
            }
        }
        for(Player p : players) {
            if(!randomStartPlat) {
                for(Platform plat : platforms) {
                    if(plat.getPlayer() == null) {
                        plat.setPlayer(p);
                        break;
                    }
                }
            }
            else {
                Platform platform = startPlats.get(ran.nextInt(startPlats.size()));
                p.withPlatforms(platform);
                startPlats.remove(platform);
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

        return game;
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


        if(client == null) {
            if (destination.getUnits().size() == 0) {
                destination.setPlayer(source.getPlayer());
            }
            source.getUnits().get(0).setPlatform(destination);
        }
        else {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(ServerGameController.EVENT_TYPE, ServerGameController.MOVE_EVENT);
            map.put(ServerGameController.EVENT_KEY, client.getPlayerId()+"");
            map.put(ServerGameController.SOURCEPLAT_KEY, source.getId()+"");
            map.put(ServerGameController.DESTPLAT_KEY, destination.getId()+"");
            client.newMessage(map);
        }
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

        if(client == null) {
            for(Unit u : platform.getPlayer().getUnits()) {
                if(u.getPlatform() == null) {
                    u.setPlatform(platform);
                    u.setPlayer(null);
                    return;
                }
            }
        }
        else {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(ServerGameController.EVENT_TYPE, ServerGameController.REENFORCE_EVENT);
            map.put(ServerGameController.EVENT_KEY, client.getPlayerId()+"");
            map.put(ServerGameController.DESTPLAT_KEY, platform.getId()+"");
            client.newMessage(map);
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

        if(client == null) {
            if(destination.getUnits().size() == 0) {
                destination.setPlayer(null);
                while(source.getUnits().size() > 1) {
                    destination.setPlayer(source.getPlayer());
                    source.getUnits().get(0).setPlatform(destination);
                }
            }
        }
        else {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(ServerGameController.EVENT_TYPE, ServerGameController.ATTACK_EVENT);
            map.put(ServerGameController.EVENT_KEY, client.getPlayerId()+"");
            map.put(ServerGameController.SOURCEPLAT_KEY, source.getId()+"");
            map.put(ServerGameController.DESTPLAT_KEY, destination.getId()+"");
            client.newMessage(map);
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

    static class MyPoint {
        double x;
        double y;
        public MyPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public MyPoint sum(MyPoint other) {
           return new MyPoint(this.x + other.x, this.y + other.y);
        }

        public MyPoint difference(MyPoint other) {
            return new MyPoint(this.x - other.x, this.y - other.y);
        }
    }
    static class MyLine {
        public MyPoint start;
        public MyPoint direction;
        public MyLine(MyPoint start, MyPoint end) {
            this.start = start;
            this.direction = end.difference(start);
        }
        boolean checkCollide(MyLine line) {
            MyPoint right = line.start.difference(this.start);
            MyPoint otherDirection = new MyPoint(-line.direction.x, -line.direction.y);
            if(otherDirection.y*this.direction.x-this.direction.y*otherDirection.x == 0d) {
                return false;
            }
            double s = (right.y*this.direction.x-this.direction.y*right.x)/
                    (otherDirection.y*this.direction.x-this.direction.y*otherDirection.x);
            if(s >= 0 && s <= 1) {
                return true;
            }
            return false;
        }
        boolean checkCollide(MySquare square) {
            double x = square.position.x;
            double y = square.position.y;
            double hside = square.side/2;
            MyPoint c1 = new MyPoint(x-hside, y-hside);
            MyPoint c2 = new MyPoint(x-hside, y+hside);
            MyPoint c3 = new MyPoint(x+hside, y+hside);
            MyPoint c4 = new MyPoint(x+hside, y-hside);
            if(checkCollide(new MyLine(c1, c2))) {
                return true;
            }
            if(checkCollide(new MyLine(c2, c3))) {
                return true;
            }
            if(checkCollide(new MyLine(c3, c4))) {
                return true;
            }
            if(checkCollide(new MyLine(c4, c1))) {
                return true;
            }
            return false;
        }
    }
    static class MySquare {
        MyPoint position;
        double side;
        public MySquare(MyPoint position, double side) {
            this.position = position;
            this.side = side;
        }
    }

    static public List<Platform> createRandomMap(int mapSizeX, int mapSizeY, int platSize, int playerCount) {
        int triesLeft = 2000;
        ArrayList<Platform> platforms = new ArrayList<>();
        outer: while(platforms.size() != playerCount * 3) {
            if(triesLeft <0) {
                return createRandomMap((int)(mapSizeX*1.1), (int)(mapSizeY*1.1), (int)(platSize*0.9), playerCount);
            }
            // Create Platform
            Point2D randomPoint = randomPosition(mapSizeX - platSize, mapSizeY - platSize);
           // Intersect with other link
            for(Platform p1 : platforms) {
                for(Platform p2 : p1.getNeighbors()) {
                        MyLine line = new MyLine(new MyPoint(p1.getXPos(), p1.getYPos())
                                , new MyPoint( p2.getXPos(), p2.getYPos()));
                        MySquare rect = new MySquare(new MyPoint(randomPoint.x, randomPoint.y), platSize);
                        if(line.checkCollide(rect)) {
                            triesLeft--;
                            continue outer;
                        }
                }
            }
            for(Platform p : platforms) {
                // Intersect with other Platform
                if(randomPoint.distance(new Point2D((float)p.getXPos(), (float)p.getYPos())) < platSize) {
                         triesLeft--;
                        continue outer;
                }
            }
            Platform newPlat = new Platform();
            newPlat.setXPos(randomPoint.x).setYPos(randomPoint.y);
            // Create Links
            Line2D link = new Line2D();
            List<Platform> possibleLinks = new ArrayList<>();
            checkLink: for(Platform target : platforms) {
                if(target.getNeighbors().size() >= 3) continue;
                link.setLine(randomPoint, new Point2D((float)target.getXPos(), (float)target.getYPos()));
                for(Platform other1 : platforms) {
                    if(target == other1) continue;
                    Point2D other1Pos = new Point2D((float)other1.getXPos(), (float)other1.getYPos());
                    for(Platform other2 : other1.getNeighbors()) {
                        if(other2 == target) continue;
                        Line2D otherLine = new Line2D();
                        otherLine.setLine(other1Pos, new Point2D((float) other2.getXPos(), (float) other2.getYPos()));
                        if(link.intersectsLine(otherLine)) {
                            triesLeft--;
                            continue checkLink;
                        }
                        for(Platform plat : platforms) {
                            if(plat == target) continue;
                            MyLine line = new MyLine(new MyPoint(link.x1, link.y1), new MyPoint(link.x2, link.y2));
                            MySquare rect = new MySquare(new MyPoint(plat.getXPos(), plat.getYPos()), platSize);
                            if(line.checkCollide(rect)) {
                                triesLeft--;
                                continue checkLink;
                            }
                        }
                        double mLink = (link.y1-link.y2) / (link.x1 - link.x2);
                        for(Platform neighbor : target.getNeighbors()) {
                            double mOther = (neighbor.getYPos() - target.getYPos()) / (neighbor.getXPos() - target.getXPos());
                            double angle = Math.abs(Math.atan(mLink)-Math.atan(mOther));
                            if(angle < 3.14d * 0.3) {
                                triesLeft--;
                                continue checkLink;
                            }
                        }
                    }
                }
                possibleLinks.add(target);
            }
            if(possibleLinks.size() == 0 && platforms.size() !=0) {
                continue outer;
            }
            if(possibleLinks.size() != 0) {
                Platform ranTarget = possibleLinks.get(ran.nextInt(possibleLinks.size()));
                possibleLinks.remove(ranTarget);
                ranTarget.withNeighbors(newPlat);
                if (ran.nextBoolean() && possibleLinks.size() != 0) {
                    ranTarget = possibleLinks.get(ran.nextInt(possibleLinks.size()));
                    ranTarget.withNeighbors(newPlat);
                }
            }
            newPlat.setCapacity(ran.nextInt(4) + 3);
            platforms.add(newPlat);
        }
        return platforms;
    }

    static Point2D randomPosition(int mapSizeX, int mapSizeY) {
        Point2D pos = new Point2D(ran.nextFloat() * mapSizeX, ran.nextFloat() * mapSizeY);
        return pos;
    }
}