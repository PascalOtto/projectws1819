package de.uniks.liverisk.gui;

import de.uniks.liverisk.MainApp;
import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

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
    VBox sidebar;

    @FXML
    AnchorPane playground;

    static final String BUTTONSTYLE = "-fx-background-color: orange; -fx-border-color: grey; -fx-font-size: 15px;" +
            " -fx-padding: 10 20 10 20";

    private Game game;
    private ProgressBar progressBar = new ProgressBar();
    private Label winnerLabel = new Label();

    public void myInitialize(Game game, Stage primaryStage, MainApp mainApp) {
        LineUIController.playground = playground;
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

        //Winner Label
        winnerLabel.setTextFill(Paint.valueOf("gold"));
        winnerLabel.setVisible(false);
        winnerLabel.setStyle("-fx-font-size: 15px;");
        game.addPropertyChangeListener(Game.PROPERTY_winner, e->onWin());
        sidebar.getChildren().add(winnerLabel);

        //Create Round-Progress-Bar
        sidebar.getChildren().add(progressBar);
        game.addPropertyChangeListener(Game.PROPERTY_timeLeft, e->updateProgressBar());

        //Buttons
        Button nextRound = new Button("next Round");
        nextRound.setOnAction(e->onClickNextRound());
        nextRound.setStyle(BUTTONSTYLE);
        sidebar.getChildren().add(nextRound);

        Button menue = new Button("Start Screen");
        menue.setOnAction(e->onClickMenue(primaryStage, mainApp));
        menue.setStyle(BUTTONSTYLE);
        sidebar.getChildren().add(menue);
    }

    public void updateProgressBar() {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(1d - (double) game.getTimeLeft() / game.getTimePerRound());
            }
        });
    }

    public void onClickNextRound() {
        game.setTimeLeft(0);
    }

    public void onClickMenue(Stage primaryStage, MainApp mainApp) {
        PersistenceUtil p = new PersistenceUtil();
        p.save(game);
        try {
            mainApp.start(primaryStage);
        }
        catch(Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void onWin() {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(game.getWinner() != null) {
                    winnerLabel.setVisible(true);
                    winnerLabel.setText(game.getWinner().getName() + " wins!");
                }
            }
        });
    }
}
