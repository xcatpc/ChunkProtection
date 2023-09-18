package eu.phoenixcraft.chunkprotection.command;

import eu.phoenixcraft.chunkprotection.ChunkProtection;
import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.UUID;

import static eu.phoenixcraft.chunkprotection.core.ClaimChunk.*;

public class cp_command implements CommandExecutor {

    private final ChunkProtection plugin;
    private MySQL mysql;
    public Connection connection;

    public cp_command(ChunkProtection plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getMysql();
        this.connection = mysql.getConnection();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Your are not a player");
            return true;
        }

        Player player = (Player) sender;
        if(args.length == 0) {
            player.sendMessage("you need to use arguments: /cp <claim | unclaim>");
            return true;
        }

        // COMMAND CLAIM ----------------------------------------------------
        if(args[0].equalsIgnoreCase("claim")) {
            if (checkChunkOwnership(player, connection) == 1){
                player.sendMessage("Dieser Chunk Gehört schon dir");
                return true;

            } else if (checkChunkOwnership(player, connection) == 2) {
                player.sendMessage("Dieser Chunk gehört jemand anderem");
                return true;

            } else if (checkChunkOwnership(player, connection) == 3) {
                player.sendMessage("dieser chunk ist frei");

            } else {
                player.sendMessage("Error Bitte kontaktire das Team");
                return true;
            }

            if (addClaimChunk(player, connection)){
                player.sendMessage("Claim added successfully");
            } else {
                player.sendMessage("Error bitte das Team kontaktieren");
                return true;
            }
        }

        // COMMAND UNCLAIM ----------------------------------------------------
        else if(args[0].equalsIgnoreCase("unclaim")) {

            if (removeClaimedChunk(player, connection)){
                player.sendMessage("Succsesfuly remove the Chunk");

            } else {
                player.sendMessage("se");
            }
        }

        else if(args[0].equalsIgnoreCase("info")) {
            Player owner = getChunkOwner( getChunkID( player.getLocation()), connection );
            if(owner != null)
                player.sendMessage("The owner is: " + owner.getName());
            else
                player.sendMessage("No owner: claim with /cp claim");

        }

        return true;
    }

}