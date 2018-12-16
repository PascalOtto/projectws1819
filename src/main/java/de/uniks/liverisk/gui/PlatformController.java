package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Unit;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlatformController implements PropertyChangeListener {
    @FXML
    Polygon platformPolygon;

    @FXML
    AnchorPane meepleFigures;

    @FXML
    AnchorPane mainPane;

    Platform platform;

    public void setPlatform(Platform platform) {
        this.platform = platform;
        platform.addPropertyChangeListener(this);
        updateView();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName() == Platform.PROPERTY_player) {
            updateView();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_units) {
            updateUnitCount();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_capacity) {
            updateCapacity();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_xPos || evt.getPropertyName() == Platform.PROPERTY_yPos) {
            updatePosition();
        }
    }

    private void updateCapacity() {
        int count = platform.getCapacity();
        ObservableList<Node> children = meepleFigures.getChildren();
        for(Node n : children) {
            if(count > 0) {
                n.setVisible(true);
            }
            else {
                n.setVisible(false);
            }
            count--;
        }
    }

    public void updateView() {
        updateUnitCount();
        updateColor();
        updateCapacity();
        updatePosition();
    }

    public void updateColor() {
        if(platform.getPlayer() == null) {
            platformPolygon.setFill(Paint.valueOf("grey"));
        }
        else {
            platformPolygon.setFill(Paint.valueOf(platform.getPlayer().getColor()));
        }
    }

    public void updatePosition() {
        mainPane.setLayoutX(platform.getXPos());
        mainPane.setLayoutY(platform.getYPos());
    }

    public void updateUnitCount() {
        int count = platform.getUnits().size();
        ObservableList<Node> children = meepleFigures.getChildren();
        for(Node n : children) {
            if(count > 0 && platform.getPlayer() != null) {
                ((Circle) n).setFill(Paint.valueOf(platform.getPlayer().getColor()));
            }
            else {
                ((Circle) n).setFill(Paint.valueOf("white"));
            }
            count--;
        }
    }
}
