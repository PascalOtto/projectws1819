package de.uniks.liverisk;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {
    static final String STDFONT = "Verdana";
    static final int TITLE_SIZE = 30;
    static final int SUBTITLE_SIZE = 20;
    static final String BUTTONSTYLE = "-fx-background-color: gold; -fx-border-color: red; -fx-font-size: 20px;" +
            " -fx-padding: 10 120 10 120";
    static final Color[] DEFAULTCOLORS = {Color.RED, Color.YELLOW, Color.DARKGREEN, Color.BLUE};
    static final String[] DEFAULTNAMES = {"Georg", "Hans", "Joseph", "Chris"};

    private Button button_2Player;
    private Button button_3Player;
    private Button button_4Player;

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vbox = new VBox(30);
        VBox vboxButtons = new VBox(20);
        VBox vboxTitle = new VBox();
        vboxButtons.setStyle("-fx-background-color: white");
        vbox.setStyle("-fx-background-color: white");
        vboxTitle.setStyle("-fx-background-color: aqua");
        Label title = new Label("Welcome to LiveRisk");
        title.setFont(Font.font(STDFONT, TITLE_SIZE));
        Label subTitle = new Label("Please select the number of players for this game!");
        subTitle.setFont(Font.font(STDFONT, SUBTITLE_SIZE));
        button_2Player = new Button("Start 2-Player Game");
        button_2Player.setStyle(BUTTONSTYLE);
        button_2Player.setOnAction(e -> showPlayerSetting(e, primaryStage));
        button_3Player = new Button("Start 3-Player Game");
        button_3Player.setStyle(BUTTONSTYLE);
        button_3Player.setOnAction(e -> showPlayerSetting(e, primaryStage));
        button_4Player = new Button("Start 4-Player Game");
        button_4Player.setStyle(BUTTONSTYLE);
        button_4Player.setOnAction(e -> showPlayerSetting(e, primaryStage));
        vbox.setAlignment(Pos.CENTER);
        vboxButtons.setAlignment(Pos.CENTER);
        vboxTitle.setAlignment(Pos.CENTER);
        vboxTitle.getChildren().addAll(title, subTitle);
        vboxButtons.getChildren().addAll(button_2Player, button_3Player, button_4Player);
        vbox.getChildren().addAll(vboxTitle, vboxButtons);
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showPlayerSetting(ActionEvent e, Stage primaryStage) {
        VBox vbox = new VBox(30);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white");
        VBox vboxTitle = new VBox();
        vboxTitle.setAlignment(Pos.CENTER);
        vboxTitle.setStyle("-fx-background-color: aqua");
        Label title = new Label("Welcome to LiveRisk");
        title.setFont(Font.font(STDFONT, TITLE_SIZE));
        Label subTitle = new Label("Please choose player-names and colors");
        subTitle.setFont(Font.font(STDFONT, SUBTITLE_SIZE));
        vboxTitle.getChildren().addAll(title, subTitle);
        VBox vboxEdit = new VBox(10);
        vboxEdit.setAlignment(Pos.CENTER);
        int playerCount = 0;
        if(e.getSource() == button_2Player) {
            playerCount = 2;
        }
        else if(e.getSource() == button_3Player) {
            playerCount = 3;
        }
        else if(e.getSource() == button_4Player) {
            playerCount = 4;
        }
        for(int i = 0; i != playerCount; i++) {
            HBox hboxPlayer = new HBox(10);
            hboxPlayer.setAlignment(Pos.CENTER);
            TextField tfNamePlayer = new TextField(DEFAULTNAMES[i]);
            ColorPicker cpPlayer = new ColorPicker(DEFAULTCOLORS[i]);
            cpPlayer.setStyle("-fx-background-color: white");
            hboxPlayer.getChildren().addAll(tfNamePlayer, cpPlayer);
            vboxEdit.getChildren().addAll(hboxPlayer);
        }
        Button button_start = new Button("start");
        button_start.setStyle(BUTTONSTYLE);
        vbox.getChildren().addAll(vboxTitle, vboxEdit, button_start);
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
    }
}
