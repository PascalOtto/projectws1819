package test;

import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestModelCreation {
    @Test
    public void test() {
        Player firstp = new Player();
        Player secondp = new Player();
        Game game = GameController.init(firstp, secondp);
        Assert.assertEquals(game.getPlayers().size(), 2);
        for(Player p : game.getPlayers()) {
            Assert.assertEquals(p.getUnits().size(), 5);
            Assert.assertEquals(p.getPlatforms().size(), 1);
            Assert.assertEquals(p.getPlatforms().get(0).getUnits().size(), 1);
        }
        ArrayList<Platform> pfs = game.getPlatforms();
        Assert.assertTrue(pfs.size() >= 4);
        Assert.assertTrue(pfs.get(0).getXPos() == 50d);
        Assert.assertTrue(pfs.get(0).getYPos() == 200d);
        Assert.assertTrue(pfs.get(1).getXPos() == 650d);
        Assert.assertTrue(pfs.get(1).getYPos() == 200d);
        Assert.assertTrue(pfs.get(2).getXPos() == 300d);
        Assert.assertTrue(pfs.get(2).getYPos() == 100d);
        Assert.assertTrue(pfs.get(3).getXPos() == 400d);
        Assert.assertTrue(pfs.get(3).getYPos() == 300d);

        Assert.assertEquals(pfs.get(0).getNeighbors().size(), 2);
        Assert.assertEquals(pfs.get(1).getNeighbors().size(), 2);
        Assert.assertEquals(pfs.get(2).getNeighbors().size(), 3);
        Assert.assertEquals(pfs.get(3).getNeighbors().size(), 3);

        Assert.assertEquals(game.getCurrentPlayer(), firstp);
    }
}