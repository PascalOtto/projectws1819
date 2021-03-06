package de.uniks.liverisk.gui;

import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.input.MouseEvent;
import org.fulib.yaml.YamlIdMap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlatformUIController implements PropertyChangeListener {
    @FXML
    Polygon platformPolygon;
    @FXML
    AnchorPane meepleFigures;
    @FXML
    AnchorPane mainPane;
    @FXML
    AnchorPane selectionPane;
    Platform platform;

    static List<LineUIController> lines = new ArrayList<>();
    static int selectedPlat = -1;

    public void onMouseClicked(MouseEvent mouseEvent) {
        switch(mouseEvent.getButton()) {
            case PRIMARY:
                primaryKeyClicked();
                break;
            case SECONDARY:
                secondaryKeyClicked();
                break;
        }
    }

    private void secondaryKeyClicked() {
        if(platform.getPlayer() == platform.getGame().getCurrentPlayer() && platform.getCapacity() != platform.getUnits().size()) {
            GameController.reenforce(this.platform);

            this.platform.getGame().setSelectedPlatform(this.platform);
        }
    }

    private void primaryKeyClicked() {
        Game game = this.platform.getGame();
        if(game.getSelectedPlatform() == null) {
            game.setSelectedPlatform(this.platform);
        }
        else if(this.platform.getNeighbors().contains(game.getSelectedPlatform())
                    && game.getSelectedPlatform().getPlayer() == platform.getGame().getCurrentPlayer()) {
            if(game.getSelectedPlatform().getUnits().size() > 1) {
                if (this.platform.getPlayer() == null || game.getSelectedPlatform().getPlayer() == this.platform.getPlayer()) {
                    if(this.platform.getUnits().size() == this.platform.getCapacity()) {
                        game.setSelectedPlatform(null);
                        return;
                    }
                    GameController.move(game.getSelectedPlatform(), this.platform);
                    if(game.getSelectedPlatform().getUnits().size() == 1) {
                        game.setSelectedPlatform(this.platform);
                    }
                }
                else {
                    GameController.attack(game.getSelectedPlatform(), this.platform);
                    if(this.platform.getPlayer() == game.getSelectedPlatform().getPlayer()) {
                        game.setSelectedPlatform(this.platform);
                    }
                }
            }
            else {
                game.setSelectedPlatform(this.platform);
            }
        }
        else {
            game.setSelectedPlatform(this.platform);
        }
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
        platform.addPropertyChangeListener(this);
        if(this.platform.getId() == selectedPlat) {
           this.platform.getGame().setSelectedPlatform(this.platform);
        }
        updateView();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName() == Platform.PROPERTY_player) {
            updateView();
            checkWinningCondition(this.platform);
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_units) {
            updateUnitCount();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_capacity) {
            updateCapacity();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_neighbors) {
            updateNeighbors();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_xPos || evt.getPropertyName() == Platform.PROPERTY_yPos) {
            updatePosition();
        }
        else if(evt.getPropertyName() == Platform.PROPERTY_selectedBy) {
            if(this.platform.getSelectedBy() != null) {
                selectionPane.setVisible(true);
                selectedPlat = this.platform.getId();
            }
            else {
                selectionPane.setVisible(false);
            }
        }
    }

    private void updateNeighbors() {
        outer: for(Platform p : platform.getNeighbors()) {
            for (LineUIController l : lines) {
                if (l.compare(platform, p)) {
                    continue outer;
                }
            }
            //Create Line
            LineUIController linectrl = new LineUIController();
            linectrl.initialize(platform, p, 101, 86);
            lines.add(linectrl);
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
        updateCapacity();
        updatePosition();
        updateNeighbors();
        updateColor();
    }

    public void updateColor() {
        if(platform.getPlayer() == null) {
            platformPolygon.setFill(Paint.valueOf("grey"));
        }
        else {
            platformPolygon.setFill(Paint.valueOf(platform.getPlayer().getColor()));
            for(Platform n : platform.getNeighbors()) {
                if(n.getPlayer() == platform.getPlayer()) {
                    LineUIController linUI = searchLine(n);
                    if(linUI != null) {
                        linUI.setColor(getColor());
                    }
                }
            }
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

    public LineUIController searchLine(Platform p) {
        if(!this.platform.getNeighbors().contains(p)) {
            System.out.println("Critical");
        }
        for(LineUIController ctrl : lines) {
            if(ctrl.compare(platform, p)) {
                return ctrl;
            }
        }
        Logger.getGlobal().log(Level.WARNING, "Line does not exist between "+p.getNeighbors().toString()+" and "+this.platform.getNeighbors().toString());
        return null;
    }

    public Paint getColor() {
        return platformPolygon.getFill();
    }

    static public void checkWinningCondition(Platform platform) {
        ArrayList<Player> activePlayers = new ArrayList<>(platform.getGame().getPlayers());
        for(Player p : platform.getGame().getPlayers()) {
            if(p.getPlatforms().size() == 0) {
                activePlayers.remove(p);
            }
        }
        if(activePlayers.size() == 1) {
            platform.getGame().setWinner(activePlayers.get(0));
        }
    }
}
