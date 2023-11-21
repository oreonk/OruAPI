package com.oreonk;

import com.oreonk.DB.DatabaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class BlockCounter {
    DatabaseCommand db = OruAPI.getPlugin(OruAPI.class).getDatabase();
    DecimalFormat decimalFormat = new DecimalFormat("#.#");

    //Для BlockBreakEvent - считает количество всех сломанных блоков
    public void allBreakAmountHandler(Player player, double multiplier){
        String playerUUID = player.getUniqueId().toString();
        double amount = Double.parseDouble(decimalFormat.format(1+1*multiplier));
        OruAPI.getPlugin(OruAPI.class).allBlockBreak.replace(playerUUID, (OruAPI.getPlugin(OruAPI.class).allBlockBreak.get(playerUUID)) + amount);
    }

    //Хендлит счетчик типа сломаных блоков. BlockBreakEvent
    public void blockTypesHandler(Player player, double multiplier, Material material){
        double amount = Double.parseDouble(decimalFormat.format(1+1*multiplier));
        String playerUUID = player.getUniqueId().toString();
        if (!OruAPI.getPlugin(OruAPI.class).blockTypesBreak.get(playerUUID).containsKey(material.toString())){
            OruAPI.getPlugin(OruAPI.class).blockTypesBreak.get(playerUUID).put(material.toString(), amount);
        } else {
            double currentAmount = OruAPI.getPlugin(OruAPI.class).blockTypesBreak.get(playerUUID).get(material.toString());
            OruAPI.getPlugin(OruAPI.class).blockTypesBreak.get(playerUUID).replace(material.toString(), currentAmount+amount);
        }
    }

    public void logoutHandler(Player player){
         String playerUUID = player.getUniqueId().toString();
        double amount = OruAPI.getPlugin(OruAPI.class).allBlockBreak.get(playerUUID);
        //Bukkit.getScheduler().runTaskAsynchronously(OruAPI.getPlugin(OruAPI.class), () -> {
            db.genericUpdateStatementDouble("BLOCKS", "UUID", amount, playerUUID);
        //});
        OruAPI.getPlugin(OruAPI.class).allBlockBreak.remove(playerUUID);
        File statsFile = getPlayerFile(player);
        FileConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        for (Map.Entry<String,Double> entry : OruAPI.getPlugin(OruAPI.class).blockTypesBreak.get(playerUUID).entrySet()) {
                statsConfig.set("Blocks." + entry.getKey(), entry.getValue());
        }
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("[OruAPI] Ошибка при записи файла статистики игрока " + player.getName());
            e.printStackTrace();
        }
        OruAPI.getPlugin(OruAPI.class).blockTypesBreak.remove(playerUUID);
    }

    public void loginHandler(Player player){
        double amount;
        String playerUUID = player.getUniqueId().toString();
        if (!db.playerExists(player)){
            db.firstLogin(player);
            amount = 0;
        } else {
            amount = Double.parseDouble(db.genericReturnString("UUID", playerUUID, "BLOCKS"));
        }
        //Bukkit.getScheduler().runTaskAsynchronously(OruAPI.getPlugin(OruAPI.class), () -> {
            OruAPI.getPlugin(OruAPI.class).allBlockBreak.put(playerUUID, amount);
            File playerFile = getPlayerFile(player);
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            if (config.getKeys(false).isEmpty() & !OruAPI.getPlugin(OruAPI.class).blockTypesBreak.containsKey(playerUUID)){
                HashMap<String,Double> empt = new HashMap<>();
                OruAPI.getPlugin(OruAPI.class).blockTypesBreak.put(playerUUID, empt);
            } else {
                HashMap<String,Double> load = new HashMap<>();
                for (String key : config.getConfigurationSection("Blocks").getKeys(true)){
                    load.put(key, config.getDouble(key));
                }
                OruAPI.getPlugin(OruAPI.class).blockTypesBreak.put(playerUUID,load);
            }
        //});
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
}
