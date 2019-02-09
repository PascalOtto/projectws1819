package de.uniks.liverisk.event;

import de.uniks.liverisk.MainApp;
import de.uniks.liverisk.OnlineSettingsView;
import de.uniks.liverisk.gui.PersistenceUtil;
import de.uniks.liverisk.model.Game;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Client {
    Socket socket;
    OnlineSettingsView onlineSettingsView;
    MainApp mainApp;
    Game game = null;
    Executor executor = Executors.newSingleThreadExecutor();
    public Client(OnlineSettingsView onlineSettingsView,MainApp mainApp) {
        this.onlineSettingsView = onlineSettingsView;
        this.mainApp = mainApp;
    }

    public int getPlayerId() {
        return onlineSettingsView.getPlayerId();
    }

    public boolean connect(String ip) {
        try {
            socket = new Socket(ip, 42424);
            new ClientCallAgent(socket, this);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void processMessage(LinkedHashMap<String, String> map) {
        if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.PLAYERID_EVENT)) {
            onlineSettingsView.setPlayerId(Integer.parseInt(map.get(ServerGameController.EVENT_KEY))
                    , Integer.parseInt(map.get(ServerGameController.PLAYERCOUNT_KEY))
            , Integer.parseInt(map.get(ServerGameController.MAXPLAYER_KEY)));
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.PLAYERSETTING_EVENT)) {
            onlineSettingsView.setPlayerSetting(Integer.parseInt(map.get(ServerGameController.EVENT_KEY))
            ,map.get(ServerGameController.PLAYERNAME_KEY)
                 ,map.get(ServerGameController.PLAYERCOLOR_KEY));
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.STARTGAME_EVENT)) {
            Platform.runLater(()->mainApp.showOnlineGame());
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.GAMEUPDATE_EVENT)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    game  =  new PersistenceUtil().load(map.get(ServerGameController.EVENT_KEY));
                    game.setCurrentPlayer(game.getPlayers().get(getPlayerId()));
                    mainApp.updateOnlineGameUI(game);
                }
            });
        }
        else if(map.get(ServerGameController.EVENT_TYPE).equals(ServerGameController.TIME_EVENT)) {
            if(game!= null) {
                game.setTimeLeft(Integer.parseInt(map.get(ServerGameController.EVENT_KEY)));
            }
        }
    }

    public void newMessage(LinkedHashMap<String, String> map) {executor.execute(()->doNewMessage(map));}

    private void doNewMessage(LinkedHashMap<String, String> map) {
        try
        {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
            objOut.writeObject(map);
            objOut.flush();
        }
        catch (IOException e)
        {
            System.out.println("ups");
        }
    }

    public void disconnect() {
        if(socket == null) return;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
