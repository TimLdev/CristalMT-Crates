package dev.tim.crates.util;

public class StringIntegerUtil {

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

}
