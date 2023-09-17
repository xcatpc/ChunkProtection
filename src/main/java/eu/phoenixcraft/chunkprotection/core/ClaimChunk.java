package eu.phoenixcraft.chunkprotection.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ClaimChunk {


    public static int getChunkID(Location location) {
        int chunkX = location.getBlockX() >> 4; // Verschieben Sie die X-Koordinate um 4 Bits, um den Chunk zu erhalten
        int chunkZ = location.getBlockZ() >> 4; // Verschieben Sie die Z-Koordinate um 4 Bits, um den Chunk zu erhalten
        return chunkX + (chunkZ << 16); // Kombinieren Sie die X- und Z-Koordinaten, um die Chunk-ID zu erstellen
    }

    public static boolean addClaimChunk(UUID playerUUID, int chunkID, Connection connection){
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

    public static boolean removeClaimedChunk(UUID playerUUID, int chunkID, Connection connection) {
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
    // give the owner of a chunk
    // 1 == gehört dem Spieler
    // 2 == gehört jemand anderem
    // 3 == ist noch frei
    public static int checkChunkOwnership(UUID playerUUID, int chunkID, Connection connection) {
        String query = "SELECT * FROM claimed_chunks WHERE chunk_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, chunkID);

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
