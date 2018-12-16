package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Player;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class PlayerCardController {
    @FXML
    Label labelPlayerName;

    @FXML
    HBox meepleContainer;

    @FXML
    Rectangle colorBox;

    Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        labelPlayerName.setText(player.getName());
        colorBox.setFill(Paint.valueOf(player.getColor()));
        player.addPropertyChangeListener("units", e -> update());
        ObservableList<Node> children = meepleContainer.getChildren();
        boolean active = true;
        int i = 0;
        for(Node n : children) {
            if(player.getUnits().size() == i) {
                active = false;
            }
            Circle circle = (Circle) n;
            circle.setVisible(active);
            circle.setFill(Paint.valueOf(player.getColor()));
            i++;
        }
    }

    public void update() {
        ObservableList<Node> children = meepleContainer.getChildren();
        boolean active = true;
        int i = 0;
        for(Node n : children) {
            if(player.getUnits().size() == i) {
                active = false;
            }
            Circle circle = (Circle) n;
            circle.setVisible(active);
            i++;
        }
    }
}
