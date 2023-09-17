package eu.phoenixcraft.chunkprotection.command;

import eu.phoenixcraft.chunkprotection.ChunkProtection;
import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.UUID;

import static eu.phoenixcraft.chunkprotection.core.ClaimChunk.*;

public class Unclaim implements CommandExecutor {


    private final ChunkProtection plugin;
    private MySQL mysql;
    private Connection connection;

    public Unclaim(ChunkProtection plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getMysql();
        this.connection = mysql.getConnection();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Du bist kein player");
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        UUID playerUUID = player.getUniqueId();
        Location location = player.getLocation();
        int chunkID = getChunkID(location);

        if (checkChunkOwnership(playerUUID, chunkID, connection) == 1){
            player.sendMessage("Dieser Chunk Gehört schon dir");
            return true;

        } else if (checkChunkOwnership(playerUUID, chunkID, connection) == 2) {
            player.sendMessage("Dieser Chunk gehört jemand anderem");
            return true;

        } else if (checkChunkOwnership(playerUUID, chunkID, connection) == 3) {
            player.sendMessage("dieser chunk ist frei");

        } else {
            player.sendMessage("Error Bitte kontaktire das Team");
            return true;
        }


        if (removeClaimedChunk(playerUUID, chunkID, connection)){
            player.sendMessage("Succsesfuly remove the Chunk");

        } else {
            player.sendMessage("se");
        }


        return true;
    }
}
