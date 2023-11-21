package com.oreonk;

import com.oreonk.DB.DatabaseCommand;
import com.oreonk.DB.SQLite;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.HashMap;

public class OruAPI extends JavaPlugin {
    private DatabaseCommand db;
    public HashMap<String, Double> allBlockBreak = new HashMap<>() {};
    public HashMap<String, HashMap<String, Double>> blockTypesBreak = new HashMap<>() {};
    public BlockCounter blockCounter;
    @Override
    public void onEnable() {
        this.db = new SQLite(this);
        this.db.load();
        blockCounter = new BlockCounter();
    }
    public DatabaseCommand getDatabase(){ return this.db; }

    public double GetOnlinePlayerBlockCounter(Player player){
        return allBlockBreak.get(player.getUniqueId().toString());
    }

    public HashMap<String, Double> GetOnlinePlayerBlockStats(Player player){
        return blockTypesBreak.get(player.getUniqueId().toString());
    }
}
