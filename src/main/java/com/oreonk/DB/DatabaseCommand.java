package com.oreonk.DB;

import com.oreonk.OruAPI;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Level;

public abstract class DatabaseCommand {
    OruAPI plugin;
    Connection connection;
    String table = "BlockCounter"; //Имя таблицы

    public DatabaseCommand(OruAPI instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE NAME = ?");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    public void close(PreparedStatement ps, ResultSet rs){
        try{
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }




    public void genericUpdateStatementDouble(String setWhatColumn, String setWhere, double whatEquals, String whereEquals){
        try{
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE " + table + " SET " + setWhatColumn + "=? WHERE " + setWhere + "=?");
            ps.setDouble(1, whatEquals);
            ps.setString(2, whereEquals);
            ps.executeUpdate();
            close(ps,null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String genericReturnString(String whereColumn, String whereEquals, String columnToReturn){
        try{
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + whereColumn + "=?");
            ps.setString(1, whereEquals);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String string = rs.getString(columnToReturn);
                close(ps,rs);
                return string;
            }
            close(ps,rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }



    public void firstLogin(Player player){
        try{
            connection = getSQLConnection();
            if (!playerExists(player)){
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + table + " (NAME,UUID,BLOCKS) VALUES (?,?,?)");
                ps.setString(1, player.getName());
                ps.setString(2, player.getUniqueId().toString());
                ps.setDouble(3, 0);
                ps.executeUpdate();
                close(ps,null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(Player player){
        Connection connection;
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                close(ps,rs);
                return true;
            }
            close(ps,rs);
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
