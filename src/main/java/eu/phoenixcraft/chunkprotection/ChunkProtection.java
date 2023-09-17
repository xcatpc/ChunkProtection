package eu.phoenixcraft.chunkprotection;

import eu.phoenixcraft.chunkprotection.command.Claim;
import eu.phoenixcraft.chunkprotection.command.Unclaim;
import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChunkProtection extends JavaPlugin {

    private MySQL mysql;
    @Override
    public void onEnable() {
        // Plugin startup logic

        this.mysql = new MySQL();

        if (mysql.connect()){
            getLogger().info("Database Connect");
        } else {
            getLogger().info("Database Disconnect");
        }

        //get Commands
        getCommand("claim").setExecutor(new Claim(this));
        getCommand("unclaim").setExecutor(new Unclaim(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mysql.disconnect();

    }

    public MySQL getMysql(){
        return this.mysql;
    }




}
