package dev.nova.coremc.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("msg")){
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED+"Only players can execute this command!");
                return true;
            }

            Player player = (Player) sender;


        }
        return true;
    }
}
