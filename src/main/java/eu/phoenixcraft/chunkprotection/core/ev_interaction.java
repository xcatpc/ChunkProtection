package eu.phoenixcraft.chunkprotection.core;

import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.block.Block;


import java.util.HashMap;

import static eu.phoenixcraft.chunkprotection.core.ClaimChunk.getChunkID;
import static eu.phoenixcraft.chunkprotection.core.ClaimChunk.getChunkOwner;

public class ev_interaction implements Listener {

    private final HashMap<Player, Long> lastChunkID = new HashMap<>();


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Player owner = getChunkOwner(getChunkID(block.getLocation()), MySQL.connection);

        if (owner == null || !owner.getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't have permissions here!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null) { // Könnte null sein, wenn der Spieler die Luft klickt
            Player owner = getChunkOwner(getChunkID(block.getLocation()), MySQL.connection);

            if (owner == null || !owner.getUniqueId().equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You don't have permissions here!");
            }
        }
    }



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long currentChunkID = getChunkID(player.getLocation());

        if (!lastChunkID.containsKey(player) || lastChunkID.get(player) != currentChunkID) {
            Player owner = getChunkOwner(currentChunkID, MySQL.connection);

            if (owner != null) {
                player.sendMessage("The owner of this chunk is: " + owner.getName());
            } else {
                //player.sendMessage("This chunk is unowned. You can claim with /cp claim");
            }

            lastChunkID.put(player, currentChunkID);
        }
    }

}
