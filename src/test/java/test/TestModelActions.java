package test;

import de.uniks.liverisk.logic.GameController;
import de.uniks.liverisk.model.*;
import org.junit.Assert;
import org.junit.Test;

public class TestModelActions {
    @Test
    /*Alice bewegt eine Einheit
    GameController::move(Platform source, Platform destination)
    Start: Alice hat auf Plattform 1 fünf Einheiten. Auf Plattform 2 sind noch keine Einheiten.
    Plattform eins und zwei sind verbunden.
    Action: Alice wählt Plattform 2 auf um dort hin zu ziehen.
    End: Alice hat auf Plattform 1 zwei Einheiten und auf Plattform 2 drei Einheiten.*/
    public void testMove() {
        Player Alice = new Player();
        Player player2 = new Player();
        Game game = GameController.init(Alice, player2);

        Platform platform1 = Alice.getPlatforms().get(0);
        Platform platform2 = platform1.getNeighbors().get(0);
        for(int i = 0; i != 4; i++) {
            new Unit().setPlayer(Alice).setPlatform(platform1);
        }
        Assert.assertEquals(platform1.getUnits().size(), 5);
        Assert.assertEquals(platform2.getPlayer(), null);
        Assert.assertEquals(platform2.getUnits().size(), 0);
        GameController.move(null, null);
        GameController.move(platform1, platform1);
        GameController.move(platform1, player2.getPlatforms().get(0));
        Assert.assertEquals(platform2.getPlayer(), null);
        Assert.assertEquals(platform2.getUnits().size(), 0);
        GameController.move(platform1, platform2);
        GameController.move(platform1, platform2);
        GameController.move(platform1, platform2);
        Assert.assertEquals(platform1.getUnits().size(), 2);
        Assert.assertEquals(platform2.getUnits().size(), 3);
        Assert.assertEquals(platform2.getPlayer(), Alice);
    }

    @Test
    /*
    Bob greift Alice an
    GameController::attack(Platform source, Platform destination)
    Start: Bob hat drei Einheiten auf einer Plattform, welche an eine Plattform von Alice
    angrenzt, auf der sich eine Einheit befindet.
    Action: Bob greift Alice Plattform von seiner Plattform aus an.
    End: Bob verliert eine Einheit, Alice verliert eine Einheit, Bob hat eine Einheit auf der
    angreifenden und eine Einheit auf der angegriffenen Plattform.
     */
    public void testAttack() {
        Player bob = new Player();
        Player alice = new Player();
        GameController gc = new GameController();
        Game game = gc.init(bob, alice);
        Platform source = bob.getPlatforms().get(0);
        Platform destination = alice.getPlatforms().get(0);
        source.withUnits(new Unit(), new Unit());

        gc.attack(source, source);
        gc.attack(null, null);
        gc.attack(source, destination);
        source.withNeighbors(destination);
        gc.attack(source, destination);
        Assert.assertEquals(destination.getPlayer(), bob);
        Assert.assertEquals(destination.getUnits().size(), 1);
        Assert.assertEquals(source.getUnits().size(), 1);
    }

    @Test
    /*
    Alice verstärkt eine Plattform
    GameController::reenforce(Platform platform)
    Start: Alice hat eine Plattform mit einer Kapazität von drei mit einer Einheit besetzt.
    Alice hat eine Einheit in ihrem Vorrat.
    Action: Alice sendet eine Einheit aus ihrem Vorrat auf die Plattform.
    End: Alice hat zwei Einheiten auf der Plattform. Alice hat keine Einheit in ihrem Vorrat.
     */
    public void testReenforce() {
        Player alice = new Player();
        GameController gc = new GameController();
        Game game = gc.init(alice, new Player());
        Platform platform = alice.getPlatforms().get(0);
        platform.setCapacity(3);
        alice.withoutUnits(alice.getUnits().clone());
        alice.withUnits(new Unit());
        gc.reenforce(platform);
        Assert.assertEquals(platform.getUnits().size(), 2);
        for(Unit u : alice.getUnits()) {
            Assert.assertFalse(u.getPlatform() == null);
        }
    }
}