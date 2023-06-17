package dev.tim.crates.util;

import org.bukkit.ChatColor;

public class ChatUtil {

    public static String translate(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
