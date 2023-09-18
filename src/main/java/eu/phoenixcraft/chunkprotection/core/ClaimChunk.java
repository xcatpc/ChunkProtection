package eu.phoenixcraft.chunkprotection.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ClaimChunk {


    public static int getChunkID(Location location) {

        long longchunk = location.getChunk().getChunkKey();
        int chunkX = location.getChunk().getX() >> 4;
        int chunkZ = location.getChunk().getZ() >> 4;
        return chunkX + (chunkZ << 16);
    }

    public static boolean addClaimChunk(Player player, Connection connection) {
        String insertQuery = "INSERT INTO claimed_chunks (player_uuid, chunk_id, world_name) VALUES (?, ?, ?)";

        UUID playerUUID = player.getUniqueId();
        Location location = player.getLocation();
        String worldName = player.getWorld().getName();
        int chunkID = getChunkID(location);

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, chunkID);
            statement.setString(3, worldName);

            int rowsAffected = statement.executeUpdate();

            // Überprüfen, ob die Zeile erfolgreich eingefügt wurde (1 bedeutet Erfolg)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Bei einem Fehler wird false zurückgegeben
        }
    }


    public static boolean removeClaimedChunk(Player player, Connection connection) {
        String deleteQuery = "DELETE FROM claimed_chunks WHERE player_uuid = ? AND chunk_id = ? AND world_name = ?";

        UUID playerUUID = player.getUniqueId();
        Location location = player.getLocation();
        String worldName = player.getWorld().getName();
        int chunkID = getChunkID(location);

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, chunkID);
            statement.setString(3, worldName);

            int rowsAffected = statement.executeUpdate();

            // Überprüfen, ob die Zeile erfolgreich gelöscht wurde (1 bedeutet Erfolg)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Bei einem Fehler wird false zurückgegeben
        }
    }

    // give the owner of a chunk
    // 1 == gehört dem Spieler
    // 2 == gehört jemand anderem
    // 3 == ist noch frei

    public static int checkChunkOwnership(Player player, Connection connection) {
        String query = "SELECT * FROM claimed_chunks WHERE chunk_id = ? AND world_name = ?";

        UUID playerUUID = player.getUniqueId();
        Location location = player.getLocation();
        String worldName = player.getWorld().getName();
        int chunkID = getChunkID(location);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, chunkID);
            statement.setString(2, worldName);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Chunk ist bereits beansprucht

                if (playerUUID.toString().equals(resultSet.getString("player_uuid"))) {
                    // Chunk gehört dem Spieler
                    return 1;
                } else {
                    // Chunk gehört einem anderen Spieler
                    return 2;
                }
            } else {
                // Chunk ist noch frei
                return 3;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Fehler bei der Abfrage
            return -1;
        }
    }


    public static Player getChunkOwner(int chunkID, Connection connection) {
        String query = "SELECT player_uuid FROM claimed_chunks WHERE chunk_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, chunkID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Chunk ist bereits beansprucht
                String playerUUIDString = resultSet.getString("player_uuid");

                // Finde den Spieler basierend auf seiner UUID
                Player chunkOwner = Bukkit.getPlayer(UUID.fromString(playerUUIDString));

                if (chunkOwner != null && chunkOwner.isOnline()) {
                    // Der Spieler ist online und der Chunk gehört ihm
                    return chunkOwner;
                } else {
                    // Der Spieler ist offline oder nicht gefunden
                    return null;
                }
            } else {
                // Chunk ist noch frei
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Fehler bei der Abfrage
            return null;
        }
    }


}
