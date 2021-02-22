package dev.nova.coremc.rank;

import dev.nova.coremc.CoreMC;
import dev.nova.coremc.player.configuration.PlayerConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rank {

    private final String codeName;
    private final String prefix;
    private final List<String> permissions;

    public Rank(String codeName, String prefix, List<String> permissions) {
        this.codeName = codeName;
        this.prefix = prefix;
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getCodeName() {
        return codeName;
    }

    /**
     * @return The prefix of the rank.
     */
    public String getPrefix() {
        return prefix;
    }

    public static class RankPlayer {

        private final Player player;
        private final PlayerConfigurationManager.PlayerConfiguration configuration;
        private Rank rank;

        public RankPlayer(Player player){
            this.player = player;
            this.configuration = PlayerConfigurationManager.getPlayerConfiguration(player);
            this.rank = Rank.getRank(configuration.getConfiguration().getString("rank"));

            for(String permission: rank.getPermissions()){
                PermissionAttachment permissionAttachment = player.addAttachment(CoreMC.getPlugin(CoreMC.class));
                permissionAttachment.setPermission(permission,true);
                List<PermissionAttachment> permissionAttachments = (CoreMC.getAttachments().get(this) != null) ? CoreMC.getAttachments().get(this) : new ArrayList<>();
                CoreMC.getAttachments().put(this,permissionAttachments);
            }
            player.setPlayerListName(rank.getPrefix()+" "+player.getName());
        }

        public Player getPlayer() {
            return player;
        }

        public PlayerConfigurationManager.PlayerConfiguration getConfiguration() {
            return configuration;
        }

        public Rank getRank(){
            return rank;
        }

        public void setRank(Rank rank) {
            configuration.getConfiguration().set("rank",rank.getCodeName());
            configuration.save();
            this.rank = rank;
            player.setPlayerListName(rank.getPrefix()+" "+player.getName());
        }
    }

    public static class RankManager implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent event){

        }
        @EventHandler
        public void onQuit(PlayerQuitEvent event){
            RankPlayer player = getRankPlayer(event.getPlayer());
            players.remove(player);
        }

        @EventHandler
        public void onChat(AsyncPlayerChatEvent event){
            RankPlayer player = getRankPlayer(event.getPlayer());
            event.setFormat(player.getRank().getPrefix()+" "+player.getPlayer().getName()+ChatColor.WHITE+": "+event.getMessage());
        }

        private final FileConfiguration configuration;

        public RankManager(FileConfiguration configuration) {
            this.configuration = configuration;
        }

        public FileConfiguration getConfiguration() {
            return configuration;
        }

        public Rank getDefaultRank() {
            return Rank.getRank(configuration.getString("default_rank"));
        }

        public static void loadRanks(File dest) {
            Bukkit.getConsoleSender().sendMessage("[RANKS] " + ChatColor.DARK_AQUA + "Looking for ranks in: " + dest.getName());
            for (File file : Objects.requireNonNull(dest.listFiles())) {
                if (file.isDirectory()) {
                    loadRanks(file);
                } else {
                    if (!file.getName().startsWith("-")) {
                        if (file.getName().endsWith(".yml")) {
                            loadRank(file);
                        } else {
                            Bukkit.getConsoleSender().sendMessage("[RANKS] " + ChatColor.RED + "Invalid file type: '" + file.getName() + "'");
                        }
                    }
                }
            }
        }

        public static boolean loadRank(File file) {
            Bukkit.getConsoleSender().sendMessage("[RANKS] "+ChatColor.GREEN+" Loading rank: "+file.getName());
            try {
                YamlConfiguration configuration = new YamlConfiguration();
                configuration.load(file);

                if (!configuration.contains("prefix") && !configuration.contains("color")) {
                    Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName()+ "(Does not contain a prefix or a color!)");
                    return false;
                }
                if(!configuration.contains("codeName")){
                    Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName()+ "(Does not contain a codeName!)");
                    return false;
                }
                boolean determinedPrefix = false;
                String prefix = "";
                if(configuration.contains("color")) {
                    if (!determinedPrefix){
                        if (!configuration.get("color").getClass().getTypeName().equalsIgnoreCase("java.lang.String")) {
                            Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName() + "(The color must be a String!)");
                            return false;
                        }else{
                            prefix = configuration.getString("color");
                            determinedPrefix = true;
                        }
                    }else{
                        Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName() + "(Determined a prefix already!)");
                        return false;
                    }
                }else if(configuration.contains("prefix")){
                    if (!determinedPrefix){
                        if (!configuration.get("prefix").getClass().getTypeName().equalsIgnoreCase("java.lang.String")) {
                            Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName() + "(The prefix must be a String!)");
                            return false;
                        }else{
                            prefix = configuration.getString("prefix");
                            determinedPrefix = true;
                        }
                    }else{
                        Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName() + "(Determined a prefix already!)");
                        return false;
                    }
                }
                String codeName = "";
                if (configuration.contains("codeName")){
                    if (!configuration.get("codeName").getClass().getTypeName().equalsIgnoreCase("java.lang.String")) {
                        Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName() + "(The codeName must be a String!)");
                        return false;
                    }else{
                        if(configuration.getString("codeName").contains(" ")){
                            Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName() + "(The codeName must not conatin a space!)");
                            return false;
                        }else{
                            codeName = configuration.getString("codeName");
                        }
                    }
                }
                prefix = prefix.replaceAll("/","");
                prefix = prefix.replaceAll("&","§");
                List<String> permissions = new ArrayList<>();
                if(configuration.contains("permissions")){
                    permissions = configuration.getStringList("permissions");
                }
                ranks.add(new Rank(codeName,prefix,permissions));

                Bukkit.getConsoleSender().sendMessage("[RANKS] "+ChatColor.GREEN+" Loaded rank: "+file.getName()+ " ("+codeName+")");

                return true;
            } catch (Exception err) {
                Bukkit.getConsoleSender().sendMessage("[RANKS]" + ChatColor.RED + " Unable to load the rank: " + file.getName());
                err.printStackTrace();
                return false;
            }
        }
    }

    private static final List<Rank> ranks = new ArrayList<>();
    private static final List<RankPlayer> players = new ArrayList<>();

    public static Rank getRank(String aDefault) {
        for (Rank rank : ranks) {
            if (rank.getCodeName().equals(aDefault)) return rank;
        }
        return null;
    }

    public static RankPlayer getRankPlayer(Player aDefault) {
        for (RankPlayer rank : players) {
            if (rank.getPlayer().equals(aDefault)) return rank;
        }
        return null;
    }

    public static List<RankPlayer> getPlayers() {
        return players;
    }

    public static List<Rank> getRanks() {
        return ranks;
    }
}
