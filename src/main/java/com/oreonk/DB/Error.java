package com.oreonk.DB;

import com.oreonk.OruAPI;

import java.util.logging.Level;

public class Error {
    public Error() {
    }

    public static void execute(OruAPI plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }

    public static void close(OruAPI plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
