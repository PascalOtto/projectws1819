package de.uniks.liverisk;

import de.uniks.liverisk.event.Client;
import de.uniks.liverisk.event.ServerGameController;
import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Player;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class OnlineSettingsView {
    Stage primaryStage;
    MainApp mainApp;
    Client client;
    TextField[] playerNames = new TextField[4];
    ColorPicker[] playerColors = new ColorPicker[4];
    ArrayList<ObjectProperty<Color>> colors = new ArrayList<>();
    Button button_start;
    int thisPlayerID = -1;
    int playerCount = 4;
    OnlineSettingsView(Stage primaryStage, MainApp mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
        this.client = client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void createView() {
        VBox vbox = new VBox(30);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white");
        VBox vboxTitle = new VBox();
        vboxTitle.setAlignment(Pos.CENTER);
        vboxTitle.setStyle("-fx-background-color: teal");
        Label title = new Label("Welcome to LiveRisk");
        title.setFont(Font.font(MainApp.STDFONT, MainApp.TITLE_SIZE));
        Label subTitle = new Label("Please choose player-names and colors");
        subTitle.setFont(Font.font(MainApp.STDFONT, MainApp.SUBTITLE_SIZE));
        vboxTitle.getChildren().addAll(title, subTitle);
        VBox vboxEdit = new VBox(10);
        vboxEdit.setAlignment(Pos.CENTER);
        for(int i = 0; i != 4; i++) {
            HBox hboxPlayer = new HBox(10);
            hboxPlayer.setAlignment(Pos.CENTER);
            playerNames[i] = new TextField();
            playerColors[i] = new ColorPicker();
            playerNames[i].setOnAction(e->onSettingChange());
            colors.add(playerColors[i].valueProperty());
            playerColors[i].setOnAction(e->onSettingChange());
            playerNames[i].setEditable(false);
            playerColors[i].setDisable(true);
            playerNames[i].setText("FREE");
            playerNames[i].setStyle("-fx-background-color: white");
            hboxPlayer.getChildren().addAll(playerNames[i], playerColors[i]);
            vboxEdit.getChildren().addAll(hboxPlayer);
        }
        HBox playerAdder = new HBox();
        Button removePlayer = new Button("remove");
        Button addPlayer = new Button("add");
        removePlayer.setOnAction(e->sendMaxPlayerChange(false));
        addPlayer.setOnAction(e->sendMaxPlayerChange(true));
        removePlayer.setStyle("-fx-background-color: aqua; -fx-border-color: grey; -fx-font-size: 20px;");
        addPlayer.setStyle("-fx-background-color: aqua; -fx-border-color: grey; -fx-font-size: 20px;");
        playerAdder.getChildren().addAll(removePlayer, addPlayer);
        playerAdder.setAlignment(Pos.CENTER);
        button_start = new Button("Start");
        button_start.setDisable(true);
        button_start.setOnAction(e -> senShowGame());
        button_start.setStyle(MainApp.BUTTONSTYLE);
        Button backButton = new Button("back");
        backButton.setStyle("-fx-background-color: orange; -fx-border-color: grey; -fx-font-size: 20px;");
        backButton.setOnAction(e -> {GameController.client = null; client.disconnect();mainApp.start(primaryStage);});
        vbox.getChildren().addAll(vboxTitle, vboxEdit, playerAdder, button_start, backButton);
        Scene scene = new Scene(vbox, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        GameController.client = client;
    }

    private void senShowGame() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(ServerGameController.EVENT_TYPE, ServerGameController.STARTGAME_EVENT);
        client.newMessage(map);
    }

    void sendMaxPlayerChange(boolean raise) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(ServerGameController.EVENT_TYPE, ServerGameController.PLAYERID_EVENT);
        if(raise && maxPlayer < 4) {
            map.put(ServerGameController.MAXPLAYER_KEY, (maxPlayer + 1) + "");
        }
        else if(!raise && maxPlayer > 2) {
            map.put(ServerGameController.MAXPLAYER_KEY, (maxPlayer - 1) + "");
        }
        else {return;}
        client.newMessage(map);
    }
    int maxPlayer = 4;

    public void setPlayerId(int id, int count, int maxPlayer) {
        playerCount = count;
        this.maxPlayer = maxPlayer;
        System.out.println("PlayerID set to: " + id);
        thisPlayerID = id;
        for(int i = 0; i != 4; i++) {
            if(thisPlayerID == i) {
                playerNames[i].setVisible(true);
                playerColors[i].setVisible(true);
                playerNames[i].setEditable(true);
                playerColors[i].setDisable(false);
            }
            else if(i < maxPlayer) {
                playerNames[i].setVisible(true);
                playerColors[i].setVisible(true);
                playerNames[i].setEditable(false);
                playerColors[i].setDisable(true);
                if(i >= count) {
                    playerNames[i].setText("FREE");
                }
            }
            else {
                playerNames[i].setVisible(false);
                playerColors[i].setVisible(false);
            }
        }
        if(id == 0) {
            button_start.setDisable(false);
        }
    }

    public void onSettingChange() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(ServerGameController.EVENT_TYPE, ServerGameController.PLAYERSETTING_EVENT);
        map.put(ServerGameController.EVENT_KEY, thisPlayerID+"");
        map.put(ServerGameController.PLAYERNAME_KEY, playerNames[thisPlayerID].getText());
        map.put(ServerGameController.PLAYERCOLOR_KEY, colors.get(thisPlayerID).getValue().toString());
        client.newMessage(map);
    }

    public void setPlayerSetting(int id, String name, String color) {
        if(playerNames[id].getText() != name && colors.get(id).get() != Color.valueOf(color)) {
            Platform.runLater(() -> {playerNames[id].setText(name);
                colors.get(id).set(Color.valueOf(color));});
        }
    }

    public int getPlayerId() {
        return thisPlayerID;
    }
}
