package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LineUIController {
    static AnchorPane playground;
    Platform a = null,b = null;
    Line line;

    public void initialize(Platform a, Platform b, double platformWidth, double platformHeight) {
        if(a==null || b==null|| a==b) {
            Logger.getGlobal().log(Level.WARNING, "error in line initialization!");
        }

        this.a = a;
        this.b = b;
        line = new Line();
        line.setStartX(a.getXPos() + platformWidth/2);
        line.setStartY(a.getYPos() + platformHeight/2);
        line.setEndX(b.getXPos() + platformWidth/2);
        line.setEndY(b.getYPos() + platformHeight/2);
        line.setStyle("-fx-stroke: black;");
        line.setStrokeWidth(9);
        playground.getChildren().add(line);
    }

    boolean compare(Platform a, Platform b) {
       if(this.a == a && this.b == b || this.a == b && this.b == a) {
           return true;
       }
       return false;
    }

    void setColor(Paint color) {
        line.setStroke(color);
    }
}
