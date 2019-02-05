package de.uniks.liverisk;

import de.uniks.liverisk.gui.IngameUIController;
import de.uniks.liverisk.gui.PersistenceUtil;
import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Player;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {
    static final String STDFONT = "Verdana";
    static final int TITLE_SIZE = 30;
    static final int SUBTITLE_SIZE = 20;
    static final String BUTTONSTYLE = "-fx-background-color: aqua; -fx-border-color: grey; -fx-font-size: 20px;" +
            " -fx-padding: 10 120 10 120";
    static final Color[] DEFAULTCOLORS = {Color.RED, Color.YELLOW, Color.DARKGREEN, Color.BLUE};
    static final String[] DEFAULTNAMES = {"Georg", "Hans", "Joseph", "Franz"};

    private Button button_2Player;
    private Button button_3Player;
    private Button button_4Player;
    private Button loadButton;
    private Label loadingFailedLabel;

    private ArrayList<StringProperty> propertyNames = new ArrayList<>();
    private ArrayList<ObjectProperty<Color>> propertyColors = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(event -> {System.exit(0);});

        VBox vbox = new VBox(30);
        VBox vboxButtons = new VBox(20);
        VBox vboxTitle = new VBox();
        vboxButtons.setStyle("-fx-background-color: white");
        vbox.setStyle("-fx-background-color: white");
        vboxTitle.setStyle("-fx-background-color: teal");
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
        loadButton = new Button("Load");
        loadButton.setStyle("-fx-background-color: orange; -fx-border-color: grey; -fx-font-size: 20px;");
        loadButton.setOnAction(e -> loadGame(e, primaryStage));
        loadingFailedLabel = new Label("Loading failed!");
        loadingFailedLabel.setTextFill(Paint.valueOf("red"));
        loadingFailedLabel.setVisible(false);
        vbox.setAlignment(Pos.CENTER);
        vboxButtons.setAlignment(Pos.CENTER);
        vboxTitle.setAlignment(Pos.CENTER);
        vboxTitle.getChildren().addAll(title, subTitle);
        vboxButtons.getChildren().addAll(button_2Player, button_3Player, button_4Player, loadButton, loadingFailedLabel);
        vbox.getChildren().addAll(vboxTitle, vboxButtons);
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showPlayerSetting(ActionEvent event, Stage primaryStage) {
        VBox vbox = new VBox(30);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white");
        VBox vboxTitle = new VBox();
        vboxTitle.setAlignment(Pos.CENTER);
        vboxTitle.setStyle("-fx-background-color: teal");
        Label title = new Label("Welcome to LiveRisk");
        title.setFont(Font.font(STDFONT, TITLE_SIZE));
        Label subTitle = new Label("Please choose player-names and colors");
        subTitle.setFont(Font.font(STDFONT, SUBTITLE_SIZE));
        vboxTitle.getChildren().addAll(title, subTitle);
        VBox vboxEdit = new VBox(10);
        vboxEdit.setAlignment(Pos.CENTER);
        int playerCount = 0;
        if(event.getSource() == button_2Player) {
            playerCount = 2;
        }
        else if(event.getSource() == button_3Player) {
            playerCount = 3;
        }
        else if(event.getSource() == button_4Player) {
            playerCount = 4;
        }
        for(int i = 0; i != playerCount; i++) {
            HBox hboxPlayer = new HBox(10);
            hboxPlayer.setAlignment(Pos.CENTER);
            TextField tfNamePlayer = new TextField(DEFAULTNAMES[i]);
            propertyNames.add(tfNamePlayer.textProperty());
            ColorPicker cpPlayer = new ColorPicker(DEFAULTCOLORS[i]);
            propertyColors.add(cpPlayer.valueProperty());
            cpPlayer.setStyle("-fx-background-color: white");
            hboxPlayer.getChildren().addAll(tfNamePlayer, cpPlayer);
            vboxEdit.getChildren().addAll(hboxPlayer);
        }
        Button button_start = new Button("Start");
        button_start.setOnAction(e -> showGame(e, primaryStage, null));
        button_start.setStyle(BUTTONSTYLE);
        vbox.getChildren().addAll(vboxTitle, vboxEdit, button_start);
        Scene scene = new Scene(vbox, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    public void showGame(ActionEvent event, Stage primaryStage, Game loadedGame){
        Game game;
        if(loadedGame == null) {
            ArrayList<Player> player = new ArrayList<>();
            for (int i = 0; i != propertyNames.size(); i++) {
                player.add(new Player().setName(propertyNames.get(i).getValue())
                        .setColor(propertyColors.get(i).getValue().toString().substring(2)));
            }
            //game = GameController.init(player, player.get(0), GameController.createSimpleMap(), 1, "Liverisk", true);
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            game = GameController.init(player
                    , GameController.createRandomMap((int) (screenBounds.getWidth() * 0.5), (int) (screenBounds.getHeight() * 0.5), 150, player.size())
                    , 1, "Liverisk", true);

        }
        else {
            game = loadedGame;
            GameController.startGameloop(game);
        }
        propertyNames.clear();
        propertyColors.clear();

        GameController.setNonPlayerCharacters(game, game.getPlayers().get(0));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/ingame.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            ((IngameUIController)fxmlLoader.getController()).myInitialize(game);
            Scene scene = new Scene(parent, primaryStage.getWidth(), primaryStage.getHeight());
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "FXML Datei nicht gefunden!");
            e.printStackTrace();
        }
    }

    public void loadGame(ActionEvent event, Stage primaryStage) {
        PersistenceUtil persistenceUtil = new PersistenceUtil();
        Game game = persistenceUtil.load();
        if(game == null) {
            loadingFailedLabel.setVisible(true);
            return;
        }
        else {
            showGame(event, primaryStage, game);
        }
    }
}