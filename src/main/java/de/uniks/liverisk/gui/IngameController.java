package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IngameController {
    @FXML
    VBox playerList;

    @FXML
    Button testButton;

    private Game game;

    public void myInitialize(Game game) {
        testButton.setOnAction(e->test());
        this.game = game;
        for(Player p : game.getPlayers()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("playerCard.fxml"));
                Parent parent = fxmlLoader.load();
                playerList.getChildren().add(parent);
                PlayerCardController pcc = fxmlLoader.getController();
                pcc.setPlayer(p);
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "FXML Datei nicht gefunden!");
                e.printStackTrace();
            }
        }
    }

    public void test() {
        game.getPlayers().get(0).withUnits(new Unit());
    }
}
