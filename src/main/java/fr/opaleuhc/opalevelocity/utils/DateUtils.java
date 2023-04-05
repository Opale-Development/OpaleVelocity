package fr.opaleuhc.opalevelocity.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String dateFromMillis(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return sdf.format(new Date(millis));
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


    public static String getDurationIn(long millis) {
        if (millis == -1) return "permanent";

        StringBuilder sb = new StringBuilder();

        long years = TimeUnit.MILLISECONDS.toDays(millis) / 365;
        long months = TimeUnit.MILLISECONDS.toDays(millis) / 30;
        long days = TimeUnit.MILLISECONDS.toDays(millis) % 365;
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (years > 0) sb.append(years).append(" ").append("année").append((years > 1) ? "s" : "");
        if (months > 0) sb.append(sb.length() > 0 ? ", " : "").append(months).append(" ").append("mois");
        if (days > 0)
            sb.append(sb.length() > 0 ? ", " : "").append(days).append(" ").append("jour").append((days > 1) ? "s" : "");
        if (hours > 0)
            sb.append(sb.length() > 0 ? ", " : "").append(hours).append(" ").append("heure").append((hours > 1) ? "s" : "");
        if (minutes > 0)
            sb.append(sb.length() > 0 ? ", " : "").append(minutes).append(" ").append("minute").append((minutes > 1) ? "s" : "");
        if (seconds > 0)
            sb.append(sb.length() > 0 ? ", " : "").append(seconds).append(" ").append("seconde").append((seconds > 1) ? "s" : "");

        return sb.toString();
    }


}
