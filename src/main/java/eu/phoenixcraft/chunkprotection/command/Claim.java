package eu.phoenixcraft.chunkprotection.command;

import eu.phoenixcraft.chunkprotection.ChunkProtection;
import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

        if (isChunkClaimed(playerUUID, chunkID)) {
            player.sendMessage("Dieser Chunk wurde bereits beansprucht");
        } else {
            if (addClaimChunk(playerUUID, chunkID)) {
                player.sendMessage("Du hast diesen Chunk beansprucht!");
            } else {
                player.sendMessage("Beim Beanspruchen des Chunks ist ein Fehler aufgetreten.");
            }
        }

        return true;
    }

    public boolean addClaimChunk(UUID playerUUID, int chunkID){
        String insertQuery = "INSERT INTO claimed_chunks (player_uuid, chunk_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, chunkID);

            int rowsAffected = statement.executeUpdate();

            // Überprüfen, ob die Zeile erfolgreich eingefügt wurde (1 bedeutet Erfolg)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Bei einem Fehler wird false zurückgegeben
        }
    }

    public boolean removeClaimedChunk(UUID playerUUID, int chunkID) {
        String deleteQuery = "DELETE FROM claimed_chunks WHERE player_uuid = ? AND chunk_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, chunkID);

            int rowsAffected = statement.executeUpdate();

            // Überprüfen, ob die Zeile erfolgreich gelöscht wurde (1 bedeutet Erfolg)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Bei einem Fehler wird false zurückgegeben
        }
    }

    public boolean isChunkClaimed(UUID playerUUID, int chunkID) {
        String query = "SELECT * FROM claimed_chunks WHERE player_uuid = ? AND chunk_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, chunkID);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getChunkID(Location location) {
        int chunkX = location.getBlockX() >> 4; // Verschieben Sie die X-Koordinate um 4 Bits, um den Chunk zu erhalten
        int chunkZ = location.getBlockZ() >> 4; // Verschieben Sie die Z-Koordinate um 4 Bits, um den Chunk zu erhalten
        return chunkX + (chunkZ << 16); // Kombinieren Sie die X- und Z-Koordinaten, um die Chunk-ID zu erstellen
    }
}