package dev.tim.crates.util;

public enum ShulkerBox {

    WHITE_SHULKER_BOX("wit"),
    ORANGE_SHULKER_BOX("oranje"),
    MAGENTA_SHULKER_BOX("magenta"),
    LIGHT_BLUE_SHULKER_BOX("lichtblauw"),
    YELLOW_SHULKER_BOX("geel"),
    LIME_SHULKER_BOX("limoen"),
    PINK_SHULKER_BOX("roze"),
    GRAY_SHULKER_BOX("grijs"),
    SILVER_SHULKER_BOX("zilver"),
    CYAN_SHULKER_BOX("cyaan"),
    PURPLE_SHULKER_BOX("paars"),
    BLUE_SHULKER_BOX("blauw"),
    BROWN_SHULKER_BOX("bruin"),
    GREEN_SHULKER_BOX("groen"),
    RED_SHULKER_BOX("rood"),
    BLACK_SHULKER_BOX("zwart");

    private final String color;

    ShulkerBox(String color){
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
