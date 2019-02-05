package test;

import org.fulib.Fulib;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.junit.Test;

public class GenModelTest {
    @Test
    public void test() {
        ClassModelBuilder mb = Fulib.classModelBuilder("de.uniks.liverisk.model");
        ClassBuilder player = mb.buildClass("Player")
                .buildAttribute("name", mb.STRING)
                .buildAttribute("color", mb.STRING);
        ClassBuilder unit = mb.buildClass("Unit");
        ClassBuilder platform = mb.buildClass("Platform")
                .buildAttribute("capacity", mb.INT)
                .buildAttribute("xPos", mb.DOUBLE)
                .buildAttribute("yPos", mb.DOUBLE);
        ClassBuilder game = mb.buildClass("Game")
                .buildAttribute("name", mb.STRING)
                .buildAttribute("timeLeft", mb.INT)
                .buildAttribute("timePerRound", mb.INT, "10000")
                .buildAttribute("isRunning", mb.BOOLEAN, "false");

        game.buildAssociation(platform, "platforms", mb.MANY, "game", mb.ONE);
        game.buildAssociation(platform, "selectedPlatform", mb.ONE, "selectedBy", mb.ONE);
        game.buildAssociation(player, "players", mb.MANY, "game", mb.ONE);
        game.buildAssociation(player, "winner", mb.ONE, "gameWon", mb.ONE);
        game.buildAssociation(player, "currentPlayer", mb.ONE, "currentGame", mb.ONE);
        platform.buildAssociation(platform, "neighbors", mb.MANY, "neighbors", mb.MANY);
        platform.buildAssociation(player, "player", mb.ONE, "platforms", mb.MANY);
        platform.buildAssociation(unit, "units", mb.MANY, "platform", mb.ONE);
        player.buildAssociation(unit, "units", mb.MANY, "player", mb.ONE);


        Fulib.generator().generate(mb.getClassModel());
    }

}
