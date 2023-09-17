package eu.phoenixcraft.chunkprotection.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Unclaim implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Du bist kein player");
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        Location location = player.getLocation();

        if ()


        return true;
    }
}
