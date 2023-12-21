package com.oreonk;

import com.fastasyncworldedit.core.function.visitor.Order;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.function.pattern.ClipboardPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.storage.ChunkStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class SlowBuild {
    //speed - скорость постройки в виде блоков/час
    public void build(Long speed, File file, World world, Player player){
        Clipboard clipboard = null;

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))){
            clipboard = reader.read();
        } catch (Exception e){
            e.printStackTrace();
        }
        if (clipboard != null) {
            Location chunkStart = getChunkStart(player);
            BlockVector3 startLocation = BlockVector3.at(chunkStart.getBlockX(), chunkStart.getBlockY(), chunkStart.getBlockZ());
            BlockVector3 endLocation = BlockVector3.at(0,0,0);
            String facing = player.getFacing().toString();
            int height = clipboard.getHeight();
            if (clipboard.getWidth() <= 16){
                switch (facing) {
                    case "WEST" -> {
                        Location end = chunkStart.add(-15.0, height, -15);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                    case "SOUTH" -> {
                        Location end = chunkStart.add(-15.0, height, 15);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                    case "EAST" -> {
                        Location end = chunkStart.add(15.0, height, 15);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                    case "NORTH" -> {
                        Location end = chunkStart.add(15.0, height, -15);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                }
            } else {
                int xChunks = (int) Math.ceil(clipboard.getWidth()/16d)-1;
                int yChunks = (int) Math.ceil(clipboard.getLength()/16d)-1;
                switch (facing) {
                    case "WEST" -> {
                        Location end = chunkStart.add(-15 - 16 * xChunks, height, -15 - 16 * yChunks);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                    case "SOUTH" -> {
                        Location end = chunkStart.add(-15 - 16*xChunks, height, 15 + 16*yChunks);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                    case "EAST" -> {
                        Location end = chunkStart.add(15 + 16*xChunks, height, 15 + 16*yChunks);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                    case "NORTH" -> {
                        Location end = chunkStart.add(15 + 16*xChunks, height, -15 - 16*yChunks);
                        endLocation = BlockVector3.at(end.getX(), end.getY(), end.getZ());
                    }
                }
            }
            if (!endLocation.equals(BlockVector3.at(0, 0, 0))) {
                CuboidRegion region = new CuboidRegion(startLocation, endLocation);
                for (int y = clipboard.getMinimumPoint().getY(); y <= clipboard.getMaximumPoint().getY(); y++) {
                    for (int x = clipboard.getMinimumPoint().getX(); x <= clipboard.getMaximumPoint().getX(); x++) {
                        for (int z = clipboard.getMinimumPoint().getZ(); z <= clipboard.getMaximumPoint().getZ(); z++) {
                            BlockState block = clipboard.getBlock(BlockVector3.at(x, y, z));

                            Bukkit.getServer().getConsoleSender().sendMessage(block.getBlockType().toString() + " (" + x + ", " + y + ", " + z + ")");
                        }
                    }
                }
            } else {
                player.sendMessage("REGION CREATE ERROR");
            }
        }
    }
    public File getSchematicFile(String fileName){
        return new File(WorldEdit.getInstance().getSchematicsFolderPath() + File.separator + fileName + ".schem");
    }
    public Location getChunkStart(Player player){
        int y = (player.getLocation().getBlockY())-1;
        Location location = null;
        if (player.getFacing().toString().equals("WEST")){
            location = player.getLocation().getChunk().getBlock(15,y,15).getLocation();
        } else if (player.getFacing().toString().equals("SOUTH")){
            location = player.getLocation().getChunk().getBlock(15,y,0).getLocation();
        } else if (player.getFacing().toString().equals("EAST")){
            location = player.getLocation().getChunk().getBlock(0,y,0).getLocation();
        } else if (player.getFacing().toString().equals("NORTH")){
            location = player.getLocation().getChunk().getBlock(0,y,15).getLocation();
        }
        return location;
    }
    public Integer calculateBlockSpeed(int speedValue, int valueNeeded){
        int tickValue = OruAPI.getPlugin(OruAPI.class).getGlobalTickSpeed();
        int blockPlaced = valueNeeded/speedValue*tickValue;
    }
}
