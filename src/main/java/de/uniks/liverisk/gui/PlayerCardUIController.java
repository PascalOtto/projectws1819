package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Player;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class PlayerCardUIController implements PropertyChangeListener {
    @FXML
    Label labelPlayerName;

    @FXML
    HBox meepleContainer;

    @FXML
    Rectangle colorBox;

    @FXML
    ImageView skullImage;
    @FXML
    VBox mainBox;

    Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        player.addPropertyChangeListener(this);
        updateView();
    }

    public void updateView() {
       updatePlayerName();
       updatePlayerColor();
       updateUnitCount();
       updateAlive();
    }

    public void updateUnitCount() {
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

    public void updatePlayerName() {
        labelPlayerName.setText(player.getName());
    }

    public void updatePlayerColor() {
        ObservableList<Node> children = meepleContainer.getChildren();
        for(Node n : children) {
            Circle circle = (Circle) n;
            circle.setFill(Paint.valueOf(player.getColor()));
        }

        colorBox.setFill(Paint.valueOf(player.getColor()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == Player.PROPERTY_name) {
            updatePlayerName();
        }
        else if (evt.getPropertyName() == Player.PROPERTY_color) {
            updatePlayerColor();
        }
        else if (evt.getPropertyName() == Player.PROPERTY_units) {
            updateUnitCount();
        }
        else if (evt.getPropertyName() == Player.PROPERTY_platforms) {
            updateAlive();
        }
    }

    private void updateAlive() {
        if(player.getPlatforms().isEmpty()) {
            skullImage.setVisible(true);
            mainBox.setOpacity(0.5);
        }
    }
}
