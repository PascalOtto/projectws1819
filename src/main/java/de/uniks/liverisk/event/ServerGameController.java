package de.uniks.liverisk.event;

import de.uniks.liverisk.gui.PersistenceUtil;
import de.uniks.liverisk.gui.PlatformUIController;
import de.uniks.liverisk.gui.PlayerCardUIController;
import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import javafx.scene.paint.Color;
import org.fulib.yaml.YamlIdMap;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGameController {
    public static final String EVENT_TYPE = "eventType";
    public static final String PLAYERSETTING_EVENT = "playerSetting_Event";
    public static final String PLAYERID_EVENT = "playerIDEvent";
    public static final String STARTGAME_EVENT = "startGame";
    public static final String GAMEUPDATE_EVENT= "gameUpdate";
    public static final String MOVE_EVENT =  "moveEvent";
    public static final String REENFORCE_EVENT =  "reenforce";
    public static final String ATTACK_EVENT =  "attackEvent";
    public static final String TIME_EVENT = "timer";
    public static final String EVENT_KEY = "eventKey";
    public static final String PLAYERCOUNT_KEY = "playercount";
    public static final String PLAYERNAME_KEY = "playerName";
    public static final String PLAYERCOLOR_KEY = "playerColor";
    public static final String MAXPLAYER_KEY = "maxPlayerKey";
    public static final String SOURCEPLAT_KEY = "sourcePlatform";
    public static final String DESTPLAT_KEY = "destinationPlatform";
    SocketMan socketMan;
    static final String[] playerColors = {Color.RED.toString(), Color.YELLOW.toString(), Color.DARKGREEN.toString(), Color.BLUE.toString()};
    static final String[] playerNames = {"Georg", "Hans", "Joseph", "Franz"};

    int maxPlayer = 4;

    Game game;

    public int getMaxPlayer() {return maxPlayer;}

    ServerGameController(SocketMan socketMan) {
        this.socketMan = socketMan;
    }

    public void playerConnected() {
        sendPlayerID();
        sendPlayerSettings();
    }

    public void playerDisconnected() {
        sendPlayerID();
    }

    private void sendPlayerID() {
        for(int i = 0; i!=socketMan.getSocketCount(); i++) {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(EVENT_TYPE, PLAYERID_EVENT);
            map.put(EVENT_KEY, i+"");
            map.put(PLAYERCOUNT_KEY, socketMan.getSocketCount()+"");
            map.put(MAXPLAYER_KEY, maxPlayer+"");
            socketMan.newMessage(map, i);
        }
    }

    private void sendPlayerSettings() {
        for(int i = 0; i!=socketMan.getSocketCount(); i++) {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(EVENT_TYPE, PLAYERSETTING_EVENT);
            map.put(EVENT_KEY, i+"");
            map.put(PLAYERNAME_KEY, playerNames[i]);
            map.put(PLAYERCOLOR_KEY, playerColors[i]);
            socketMan.newMessage(map);
        }
    }

    public void receive (LinkedHashMap<String, String> map) {
        if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.PLAYERSETTING_EVENT)) {
            int id = Integer.parseInt(map.get(ServerGameController.EVENT_KEY));
                    String name = map.get(ServerGameController.PLAYERNAME_KEY);
                    String color = map.get(ServerGameController.PLAYERCOLOR_KEY);

                    playerColors[id] = color;
                    playerNames[id] = name;
                    sendPlayerSettings();
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.PLAYERID_EVENT)) {
            maxPlayer = Integer.parseInt(map.get(ServerGameController.MAXPLAYER_KEY));
            sendPlayerID();
            sendPlayerSettings();
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.STARTGAME_EVENT)) {
            ArrayList<Player> playerLst = new ArrayList<>();
            for(int i= 0; i != maxPlayer; i++) {
                Player p = new Player().setName(playerNames[i]).setColor(playerColors[i]);
                playerLst.add(p);
            }
            game = GameController.init(playerLst, GameController.createRandomMap(300, 300, 250, maxPlayer)
                    , 1, "liverisk", true);
            GameController.setNonPlayerCharacters(game, socketMan.getSocketCount());
            sendShowGame();
            sendGameUpdate();
            GameController.startGameloop(game, 200);
            game.addPropertyChangeListener(Game.PROPERTY_timeLeft, e->update(e));
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.MOVE_EVENT)) {
            int playerID = Integer.parseInt(map.get(ServerGameController.EVENT_KEY));
            int sourceID = Integer.parseInt(map.get(ServerGameController.SOURCEPLAT_KEY));
            int destID = Integer.parseInt(map.get(ServerGameController.DESTPLAT_KEY));
            Platform s = getPlatform(sourceID);
            Platform d = getPlatform(destID);
            if(s == null || d == null) {
                Logger.getGlobal().log(Level.WARNING, "Platforms could not be found by ID"
                        , new Exception().getStackTrace());
            }
            else {
                GameController.move(s, d);
                sendGameUpdate();
            }
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.ATTACK_EVENT)) {
            int playerID = Integer.parseInt(map.get(ServerGameController.EVENT_KEY));
            int sourceID = Integer.parseInt(map.get(ServerGameController.SOURCEPLAT_KEY));
            int destID = Integer.parseInt(map.get(ServerGameController.DESTPLAT_KEY));
            Platform s = getPlatform(sourceID);
            Platform d = getPlatform(destID);
            if(s == null || d == null) {
                Logger.getGlobal().log(Level.WARNING, "Platforms could not be found by ID"
                        , new Exception().getStackTrace());
            }
            else {
                GameController.attack(s, d);
                sendGameUpdate();
            }
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.REENFORCE_EVENT)) {
            int playerID = Integer.parseInt(map.get(ServerGameController.EVENT_KEY));
            int destID = Integer.parseInt(map.get(ServerGameController.DESTPLAT_KEY));
            Platform d = getPlatform(destID);
            if(d == null) {
                Logger.getGlobal().log(Level.WARNING, "Platforms could not be found by ID"
                        , new Exception().getStackTrace());
            }
            else {
                GameController.reenforce(d);
                sendGameUpdate();
            }
        }
    }

    public Platform getPlatform (int id) {
        for(Platform p : game.getPlatforms()) {
            if(p.getId() == id ) {
                return p;
            }
        }
        return null;
    }

    private void update(PropertyChangeEvent event) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(ServerGameController.EVENT_TYPE, ServerGameController.TIME_EVENT);
        map.put(ServerGameController.EVENT_KEY, game.getTimeLeft()+"");
        socketMan.newMessage(map);
        if((int)event.getOldValue() <= 0) {
            sendGameUpdate();
        }
    }

    private void sendGameUpdate() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(ServerGameController.EVENT_TYPE, ServerGameController.GAMEUPDATE_EVENT);
        PlatformUIController.checkWinningCondition(game.getPlatforms().get(0));
        map.put(ServerGameController.EVENT_KEY, new PersistenceUtil().getString(game));
        socketMan.newMessage(map);
    }

    private void sendShowGame() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(ServerGameController.EVENT_TYPE, ServerGameController.STARTGAME_EVENT);
        socketMan.newMessage(map);
    }
}
