package com.oreonk;

import com.oreonk.DB.DatabaseCommand;
import com.oreonk.DB.SQLite;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class OruAPI extends JavaPlugin {
    private DatabaseCommand db;
    public HashMap<String, Double> allBlockBreak = new HashMap<>() {};
    public HashMap<String, HashMap<String, Double>> blockTypesBreak = new HashMap<>() {};
    public HashMap<String, HashMap<String, Double>> mobTypes = new HashMap<>() {};
    public BlockCounter blockCounter;
    public MobsCounter mobsCounter;
    @Override
    public void onEnable() {
        this.db = new SQLite(this);
        this.db.load();
        blockCounter = new BlockCounter();
        mobsCounter = new MobsCounter();
    }

    public File getPlayerFile(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!OruAPI.getPlugin(OruAPI.class).getDataFolder().exists()){
            OruAPI.getPlugin(OruAPI.class).getDataFolder().mkdir();
        }
        File dir = new File(OruAPI.getPlugin(OruAPI.class).getDataFolder() + File.separator + "Stats");
        if (!dir.exists()){
            dir.mkdir();
        }
        File playerFile =  new File (OruAPI.getPlugin(OruAPI.class).getDataFolder() + File.separator + "Stats", uuid + ".yml");
        if (!playerFile.exists()){
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return playerFile;
    }
    public DatabaseCommand getDatabase(){ return this.db; }

    public double getOnlinePlayerBlockCounter(Player player){
        return allBlockBreak.get(player.getUniqueId().toString());
    }

    public HashMap<String, Double> getOnlinePlayerBlockStats(Player player){
        return blockTypesBreak.get(player.getUniqueId().toString());
    }

    public HashMap<String, Double> getOnlinePlayerMobKills(Player player){
        return mobTypes.get(player.getUniqueId().toString());
    }
}
