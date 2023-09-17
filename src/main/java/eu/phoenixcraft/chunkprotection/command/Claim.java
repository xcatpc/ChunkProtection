package eu.phoenixcraft.chunkprotection.command;

import eu.phoenixcraft.chunkprotection.ChunkProtection;
import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static eu.phoenixcraft.chunkprotection.core.ClaimChunk.*;

public class Claim implements CommandExecutor {

    private final ChunkProtection plugin;
    private MySQL mysql;
    private Connection connection;

    public Claim(ChunkProtection plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getMysql();
        this.connection = mysql.getConnection();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du bist kein Spieler");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        int chunkID = getChunkID(player.getLocation());

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
            player.sendMessage("Suucesfuly add Chunk");
        } else {
            player.sendMessage("Error bitte das Team kontaktieren");
            return true;
        }

        return true;
    }

}