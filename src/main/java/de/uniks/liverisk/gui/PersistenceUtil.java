package de.uniks.liverisk.gui;

import de.uniks.liverisk.model.Game;
import de.uniks.liverisk.model.Platform;
import de.uniks.liverisk.model.Player;
import de.uniks.liverisk.model.Unit;
import org.fulib.yaml.YamlIdMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenceUtil {
    private static final String SAVEGAME_YAML = "./savegame.yaml";
    private static final String ENCODING = "UTF-16";

    public void save(Game game) {
        try {
            File file = new File(SAVEGAME_YAML);
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(Paths.get(file.toURI()), getString(game).getBytes(ENCODING));
        }
        catch(Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Save method failed: " + e.toString());
        }
    }

    public String getString(Game game) {
        YamlIdMap yamlIdMap = new YamlIdMap(Game.class.getPackage().getName());
        String yaml = yamlIdMap.encode(game);
        return yaml;
    }

    public Game load() {
        try {
            File file = new File(SAVEGAME_YAML);
            if(!file.exists()) {
                throw new IOException("No file found");
            }
            byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
            String yaml = new String(bytes, ENCODING);

           return load(yaml);
        }
        catch(IOException e) {
            //Logger.getGlobal().log(Level.WARNING, "Load method failed: " + e.toString());
            return null;
        }
    }
    public Game load(String yamlString) {
        YamlIdMap yamlIdMap = new YamlIdMap(Game.class.getPackage().getName());
        Game game = new Game();
        game.setName("Liverisk"); // yaml-decode fails, if you don't do this
        yamlIdMap.decode(yamlString, game);
        return game;
    }
}
