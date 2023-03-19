package fr.opaleuhc.opalevelocity.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String dateFromMillis(long millis) {
        return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.FRANCE).format(new Date(millis));
    }

    public static long getDurationFromStr(String str) {
        long duration = 0;
        if (str.equals("perm")) return -1;
        if (str.contains("s")) duration += Long.parseLong(str.replace("s", "")) * 1000;
        else if (str.contains("m")) duration += Long.parseLong(str.replace("m", "")) * 1000 * 60;
        else if (str.contains("h")) duration += Long.parseLong(str.replace("h", "")) * 1000 * 60 * 60;
        else if (str.contains("d")) duration += Long.parseLong(str.replace("d", "")) * 1000 * 60 * 60 * 24;
        else if (str.contains("w")) duration += Long.parseLong(str.replace("w", "")) * 1000 * 60 * 60 * 24 * 7;
        else if (str.contains("M")) duration += Long.parseLong(str.replace("mo", "")) * 1000 * 60 * 60 * 24 * 30;
        else if (str.contains("y")) duration += Long.parseLong(str.replace("y", "")) * 1000 * 60 * 60 * 24 * 365;
        return duration;
    }

    public static String duration(long millis) {
        if (millis == -1) return "permanente";
        return dateFromMillis(System.currentTimeMillis() - millis);
    }


}
