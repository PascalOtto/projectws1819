package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IngameUIController {
    @FXML
    VBox playerList;

    @FXML
    AnchorPane playground;

    private Game game;

    public void myInitialize(Game game) {
        this.game = game;
        for(Player p : game.getPlayers()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_card.fxml"));
                Parent parent = fxmlLoader.load();
                playerList.getChildren().add(parent);
                PlayerCardUIController pcc = fxmlLoader.getController();
                pcc.setPlayer(p);
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "FXML Datei nicht gefunden!");
                e.printStackTrace();
            }
        }

        //Create PlatformsViews
        for(Platform plat : game.getPlatforms()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("platform.fxml"));
            try {
                Parent parent = fxmlLoader.load();
                PlatformUIController platCon = fxmlLoader.getController();
                platCon.setPlatform(plat);
                playground.getChildren().add(parent);
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "FXML Datei nicht gefunden!");
                e.printStackTrace();
            }
        }
    }
}
