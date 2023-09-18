package eu.phoenixcraft.chunkprotection.core;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ClaimChunk {


    public static int[] getPlayerChunk(Location location) {
        Chunk chunk = location.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        return new int[] { chunkX, chunkZ };
    }

    public static int[] getPlayerChunk_coo(int[] chunk_coo) {
        int startX = chunk_coo[0] * 16;
        int startZ = chunk_coo[1] * 16;
        int beginX = (startX < 0) ? startX + 15 : startX;
        int beginZ = (startZ < 0) ? startZ + 15 : startZ;
        int endX = (startX < 0) ? startX - 0 : startX + 15;
        int endZ = (startZ < 0) ? startZ - 0 : startZ + 15;
        return new int[] { beginX, endX, beginZ, endZ };
    }

    public static long getChunkID(Location location) {
        int[] chunkCoords = getPlayerChunk(location);
        long upperBits = (long) chunkCoords[0] << 32;
        long lowerBits = chunkCoords[1] & 0xFFFFFFFFL;
        long uniqueId = upperBits | lowerBits;
        return uniqueId;
    }


    public static boolean addClaimChunk(Player player, Connection connection) {
        String insertQuery = "INSERT INTO claimed_chunks (player_uuid, chunk_id, chunk_x, chunk_z, world_name) VALUES (?, ?, ?, ?, ?)";

        UUID playerUUID = player.getUniqueId();
        Location location = player.getLocation();
        String worldName = player.getWorld().getName();
        long chunkID = getChunkID(location);
        int[] chunk_coo = getPlayerChunk(location);

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setLong(2, chunkID);
            statement.setInt(3, chunk_coo[0]);
            statement.setInt(4, chunk_coo[1]);
            statement.setString(5, worldName);

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
        long chunkID = getChunkID(location);

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, playerUUID.toString());
            statement.setLong(2, chunkID);
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
        long chunkID = getChunkID(location);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, chunkID);
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


    public static Player getChunkOwner(long chunkID, Connection connection) {
        String query = "SELECT player_uuid FROM claimed_chunks WHERE chunk_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, chunkID);

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
