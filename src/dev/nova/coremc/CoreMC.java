package dev.nova.coremc;

import dev.nova.coremc.player.configuration.PlayerConfigurationManager;
import dev.nova.coremc.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoreMC extends JavaPlugin{

    private static Rank.RankManager RANK_MANAGER;
    private static HashMap<Rank.RankPlayer, List<PermissionAttachment>> ATTACHMENTS = new HashMap<>();

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        File configuration = new File(getDataFolder(),"config.yml");
        File rankFolder = new File(getDataFolder()+"/ranks");
        File playersFolder = new File(getDataFolder()+"/"+"/players");
        playersFolder.mkdirs();
        rankFolder.mkdirs();
        try{
            configuration.createNewFile();
        }catch (IOException e){
            System.out.println("PLEASE REPORT THIS!");
            e.printStackTrace();
        }



        RANK_MANAGER = new Rank.RankManager(getConfig());
        PlayerConfigurationManager.setPlayerConfigurations(new ArrayList<>());

        Rank.RankManager.loadRanks(rankFolder);

        Bukkit.getPluginManager().registerEvents(new PlayerConfigurationManager(),this);

        Bukkit.getPluginManager().registerEvents(RANK_MANAGER,this);

        for(Player player : Bukkit.getOnlinePlayers()){
            PlayerConfigurationManager.PlayerConfiguration playerConfiguration = new PlayerConfigurationManager.PlayerConfiguration(player);
            PlayerConfigurationManager.getPlayerConfigurations().add(playerConfiguration);
            Rank.RankPlayer rankPlayer = new Rank.RankPlayer(player);
            Rank.getPlayers().add(rankPlayer);
        }
    }

    public static HashMap<Rank.RankPlayer, List<PermissionAttachment>> getAttachments() {
        return ATTACHMENTS;
    }

    public static Rank.RankManager getRankManager() {
        return RANK_MANAGER;
    }
}
