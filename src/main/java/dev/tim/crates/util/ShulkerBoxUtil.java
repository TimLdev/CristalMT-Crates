package dev.tim.crates.util;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ShulkerBoxUtil {

    public static Material getShulkerBox(String color){
        for(ShulkerBox shulkerBox : ShulkerBox.values()){
            if(color.equalsIgnoreCase(shulkerBox.getColor())){
                return Material.valueOf(shulkerBox.name().toUpperCase());
            }
        }
        return null;
    }

    public static List<String> getShulkerColors(){
        List<String> colors = new ArrayList<>();
        for(ShulkerBox shulkerBox : ShulkerBox.values()){
            colors.add(shulkerBox.getColor());
        }
        return colors;
    }

}
