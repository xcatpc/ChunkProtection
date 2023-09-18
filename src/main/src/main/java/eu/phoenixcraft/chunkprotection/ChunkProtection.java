package eu.phoenixcraft.chunkprotection;

import eu.phoenixcraft.chunkprotection.command.cp_command;
import eu.phoenixcraft.chunkprotection.core.ev_interaction;
import eu.phoenixcraft.chunkprotection.core.tabCompleter;
import eu.phoenixcraft.chunkprotection.storage.MySQL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChunkProtection extends JavaPlugin {

    FileConfiguration config = getConfig();
    private MySQL mysql;
    public static String version = "0.01";

    @Override
    public void onEnable() {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_BLACK = "\u001B[30m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_PURPLE = "\u001B[35m";
        String ANSI_CYAN = "\u001B[36m";
        String ANSI_WHITE = "\u001B[37m";

        this.mysql = new MySQL("localhost", 3306, "ChunkProtection", "root", "kyper&3600");

        if (mysql.connect()){
            getLogger().info("Database Connect");
        } else {
            getLogger().info("Database Disconnect");
        }

        // Commands registrieren
        getCommand("cp").setExecutor(new cp_command(this));
        getCommand("cp").setTabCompleter(new tabCompleter());
        getServer().getPluginManager().registerEvents(new ev_interaction(), this);


        getLogger().info(ANSI_PURPLE + "   ____ _                 _    ____            _            _   _             " + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + "  / ___| |__  _   _ _ __ | | _|  _ \\ _ __ ___ | |_ ___  ___| |_(_) ___  _ __  " + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + " | |   | '_ \\| | | | '_ \\| |/ / |_) | '__/ _ \\| __/ _ \\/ __| __| |/ _ \\| '_ \\ " + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + " | |___| | | | |_| | | | |   <|  __/| | | (_) | ||  __/ (__| |_| | (_) | | | |" + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + "  \\____|_| |_|\\__,_|_| |_|_|\\_\\_|   |_|  \\___/ \\__\\___|\\___|\\__|_|\\___/|_| |_|" + ANSI_RESET);
        getLogger().info(ANSI_YELLOW + "  You are using the version: v" + version + " of ChunkProtection. <3" + ANSI_RESET);



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (mysql != null) {
            mysql.disconnect();
        }

    }

    public MySQL getMysql(){
        return this.mysql;
    }

}
