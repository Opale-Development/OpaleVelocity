package fr.opaleuhc.opalevelocity.utils;

import java.util.UUID;

public class User {

    private UUID uuid;
    private String name;
    private long first_connection;
    private long last_connection;
    private String mute;
    private String ban;
    private String wins;
    private String loses;
    private String kills;
    private String deaths;
    private Long best_jump;
    private String last_server;

    public User(UUID uuid, String name, long first_connection, long last_connection, String mute, String ban, String wins, String loses, String kills, String deaths, Long best_jump, String last_server) {
        this.uuid = uuid;
        this.name = name;
        this.first_connection = first_connection;
        this.last_connection = last_connection;
        this.mute = mute;
        this.ban = ban;
        this.wins = wins;
        this.loses = loses;
        this.kills = kills;
        this.deaths = deaths;
        this.best_jump = best_jump;
        this.last_server = last_server;
    }

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.first_connection = System.currentTimeMillis();
        this.last_connection = System.currentTimeMillis();
        this.mute = null;
        this.ban = null;
        this.wins = null;
        this.loses = null;
        this.kills = null;
        this.deaths = null;
        this.best_jump = null;
        this.last_server = null;
    }

    public static String serialize(User u) {
        return u.getUuid().toString() + ";" + u.getName() + ";" + u.getFirst_connection() + ";" + u.getLast_connection() + ";" + u.getMute() + ";" + u.getBan() + ";" + u.getWins() + ";" + u.getLoses() + ";" + u.getKills() + ";" + u.getDeaths() + ";" + u.getBest_jump() + ";" + u.getLast_server();
    }

    public static User deserialize(String s) {
        String[] args = s.split(";");
        return new User(UUID.fromString(args[0]), args[1], Long.parseLong(args[2]), Long.parseLong(args[3]), args[4], args[5], args[6], args[7], args[8], args[9],
                (args[10].equals("null") ? null : Long.parseLong(args[10])), args[11]);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFirst_connection() {
        return first_connection;
    }

    public void setFirst_connection(long first_connection) {
        this.first_connection = first_connection;
    }

    public long getLast_connection() {
        return last_connection;
    }

    public void setLast_connection(long last_connection) {
        this.last_connection = last_connection;
    }

    public String getMute() {
        return mute;
    }

    public void setMute(String author, String reason, long duration) {
        if (author != null && reason != null && duration != 0) this.mute = reason + ":" + duration + ":" + author;
        else this.mute = null;
    }

    public String parseMute(String str) {
        String[] args = str.split(":");
        this.mute = args[0] + ":" + args[1] + ":" + args[2];
        return this.mute;
    }

    public boolean isMuted() {
        return mute != null && !mute.isEmpty() && mute.split(":").length >= 3;
    }

    public String getMuteReason() {
        return mute.split(":")[0];
    }

    public long getMuteExpiration() {
        return Long.parseLong(mute.split(":")[1]);
    }

    public String getMuteAuthor() {
        return mute.split(":")[2];
    }

    public String parseBan(String str) {
        String[] args = str.split(":");
        this.ban = args[0] + ":" + args[1] + ":" + args[2];
        return this.ban;
    }

    public String getBan() {
        return ban;
    }

    public boolean isBanned() {
        return ban != null && !ban.isEmpty() && ban.split(":").length >= 3;
    }

    public void setBan(String author, String reason, long duration) {
        if (author != null && reason != null && duration != 0) this.ban = reason + ":" + duration + ":" + author;
        else this.ban = null;
    }

    public String getBanReason() {
        return ban.split(":")[0];
    }

    public long getBanExpiration() {
        return Long.parseLong(ban.split(":")[1]);
    }

    public String getBanAuthor() {
        return ban.split(":")[2];
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getLoses() {
        return loses;
    }

    public void setLoses(String loses) {
        this.loses = loses;
    }

    public String getKills() {
        return kills;
    }

    public void setKills(String kills) {
        this.kills = kills;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public Long getBest_jump() {
        return best_jump;
    }

    public void setBest_jump(long best_jump) {
        this.best_jump = best_jump;
    }

    public String getLast_server() {
        return last_server;
    }

    public void setLast_server(String last_server) {
        this.last_server = last_server;
    }

}
