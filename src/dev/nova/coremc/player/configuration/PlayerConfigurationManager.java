package dev.nova.coremc.player.configuration;

import dev.nova.coremc.CoreMC;
import dev.nova.coremc.rank.Rank;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerConfigurationManager implements Listener {

    @EventHandler
    public void onJoin(PlayerLoginEvent event){
        playerConfigurations.add(new PlayerConfiguration(event.getPlayer()));
        Rank.RankPlayer rankPlayer = new Rank.RankPlayer(event.getPlayer());
        Rank.getPlayers().add(rankPlayer);
        if(rankPlayer.getRank() == null) rankPlayer.setRank(CoreMC.getRankManager().getDefaultRank());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        PlayerConfiguration configuration = getPlayerConfiguration(event.getPlayer());
        playerConfigurations.remove(configuration);
    }

    private static List<PlayerConfiguration> playerConfigurations = new ArrayList<>();

    public static List<PlayerConfiguration> getPlayerConfigurations() {
        return playerConfigurations;
    }

    public static void setPlayerConfigurations(List<PlayerConfiguration> playerConfigurations) {
        PlayerConfigurationManager.playerConfigurations = playerConfigurations;
    }

    public static class PlayerConfiguration {
        private final Player player;
        private final File file;
        private final YamlConfiguration configuration;

        public PlayerConfiguration(Player player){
            this.player = player;
            this.file = new File("./plugins/CoreMC/players/"+player.getUniqueId().toString()+".yml");
            configuration = createConfiguration();
        }

        private YamlConfiguration createConfiguration() {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            YamlConfiguration configuration = new YamlConfiguration();
            try {
                configuration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return configuration;
        }

        public YamlConfiguration getConfiguration() {
            return configuration;
        }

        public Player getPlayer() {
            return player;
        }

        public File getFile() {
            return file;
        }

        public boolean save(){
            try {
                configuration.save(file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static PlayerConfiguration getPlayerConfiguration(Player player) {
        for(PlayerConfiguration configuration : playerConfigurations){
            if(configuration.getPlayer() == player){
                return configuration;
            }
        }
        return null;
    }
}
