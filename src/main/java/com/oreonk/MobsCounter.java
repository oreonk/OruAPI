package com.oreonk;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MobsCounter {
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    public void mobCounterHandler(Player player, double multiplier, Entity entity){
        double amount = Double.parseDouble(decimalFormat.format(1+1*multiplier));
        String playerUUID = player.getUniqueId().toString();
        String mobString;
        if (entity.getCustomName() != null){
            mobString = entity.getCustomName();
        } else {
            mobString = entity.getType().toString();
        }

        if (!OruAPI.getPlugin(OruAPI.class).mobTypes.get(playerUUID).containsKey(mobString)){
            OruAPI.getPlugin(OruAPI.class).mobTypes.get(playerUUID).put(mobString, amount);
        } else {
            double currentAmount = OruAPI.getPlugin(OruAPI.class).mobTypes.get(playerUUID).get(mobString);
            OruAPI.getPlugin(OruAPI.class).mobTypes.get(playerUUID).replace(mobString, currentAmount+amount);
        }
    }

    public void loginHandler(Player player){
        double amount;
        String playerUUID = player.getUniqueId().toString();
        //Bukkit.getScheduler().runTaskAsynchronously(OruAPI.getPlugin(OruAPI.class), () -> {
            File playerFile = OruAPI.getPlugin(OruAPI.class).getPlayerFile(player);
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            if (config.getConfigurationSection("Mobs") == null | config.getConfigurationSection("Mobs").getKeys(false).isEmpty() & !OruAPI.getPlugin(OruAPI.class).mobTypes.containsKey(playerUUID)){
                HashMap<String,Double> empt = new HashMap<>();
                OruAPI.getPlugin(OruAPI.class).mobTypes.put(playerUUID, empt);
            } else {
                HashMap<String,Double> load = new HashMap<>();
                for (String key : config.getConfigurationSection("Mobs").getKeys(true)){
                    load.put(key, config.getDouble(key));
                }
                OruAPI.getPlugin(OruAPI.class).mobTypes.put(playerUUID,load);
            }
        //});
    }

    public void logoutHandler(Player player){
        String playerUUID = player.getUniqueId().toString();;
        File statsFile = OruAPI.getPlugin(OruAPI.class).getPlayerFile(player);
        FileConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        for (Map.Entry<String,Double> entry : OruAPI.getPlugin(OruAPI.class).mobTypes.get(playerUUID).entrySet()) {
            statsConfig.set("Mobs." + entry.getKey(), entry.getValue());
        }
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("[OruAPI] Ошибка при записи файла статистики игрока " + player.getName());
            e.printStackTrace();
        }
        OruAPI.getPlugin(OruAPI.class).mobTypes.remove(playerUUID);
    }
}
