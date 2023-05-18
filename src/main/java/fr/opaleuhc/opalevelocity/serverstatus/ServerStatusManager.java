package fr.opaleuhc.opalevelocity.serverstatus;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.opaleuhc.opalevelocity.OpaleVelocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerStatusManager {

    public static final long NO_PING_BEFORE_GOING_OFF = 5000;
    public static final MinecraftChannelIdentifier STATUT_CHANNEL = MinecraftChannelIdentifier.from("opaleuhc:srvstatus");
    public static ServerStatusManager INSTANCE;
    private final HashMap<String, ServerStatus> serversStatus = new HashMap<>();
    private final HashMap<String, Long> lastPing = new HashMap<>();
    private final HashMap<String, Integer> playersCount = new HashMap<>();

    public ServerStatusManager() {
        INSTANCE = this;

        OpaleVelocity.instance.getProxy().getChannelRegistrar().register(STATUT_CHANNEL);

        OpaleVelocity.instance.getProxy().getScheduler().buildTask(OpaleVelocity.instance, () -> {
            pingServers();
            sendStatusForEveryServers();
        }).repeat(2, TimeUnit.SECONDS).schedule();
    }

    public void pingServers() {
        CompletableFuture.runAsync(() -> {
            for (RegisteredServer rs : OpaleVelocity.instance.getProxy().getAllServers()) {
                final int pOnline = rs.getPlayersConnected().size();
                rs.ping().whenComplete((ping, throwable) -> {
                    if (throwable != null) {
                        serversStatus.put(rs.getServerInfo().getName(), ServerStatus.OFF);
                        lastPing.put(rs.getServerInfo().getName(), System.currentTimeMillis());
                        playersCount.put(rs.getServerInfo().getName(), -1);
                    } else {
                        serversStatus.put(rs.getServerInfo().getName(), ServerStatus.ON);
                        lastPing.put(rs.getServerInfo().getName(), System.currentTimeMillis());
                        playersCount.put(rs.getServerInfo().getName(), pOnline);
                    }
                });
            }
        });
    }

    public int getPlayerCount(String serverName) {
        return playersCount.getOrDefault(serverName, -1);
    }

    public boolean isServerOnline(String serverName) {
        return serversStatus.get(serverName) == ServerStatus.ON && lastPing.containsKey(serverName) && System.currentTimeMillis() - lastPing.get(serverName) < NO_PING_BEFORE_GOING_OFF;
    }

    public ServerStatus getServerStatus(String serverName) {
        return isServerOnline(serverName) ? ServerStatus.ON : ServerStatus.OFF;
    }

    public void sendStatusForEveryServers() {
        CompletableFuture.runAsync(() -> lastPing.keySet().forEach(serverName -> {
            for (Player player : OpaleVelocity.instance.getProxy().getAllPlayers()) {
                if (player.getCurrentServer().isEmpty()) continue;
                if (player.getCurrentServer().get().getServer().getServerInfo().getName().equals(serverName)) continue;
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(serverName);
                out.writeUTF(getServerStatus(serverName).name());
                int playerCount = getPlayerCount(serverName);
                if (playerCount != -1) out.writeUTF(String.valueOf(playerCount));
                Optional<ServerConnection> serverConnection = player.getCurrentServer();
                serverConnection.ifPresent(connection -> connection.sendPluginMessage(STATUT_CHANNEL, out.toByteArray()));
                break;
            }
        }));
    }

    public ArrayList<String> getServers(String server) {
        ArrayList<String> servers = new ArrayList<>();
        for (String s : serversStatus.keySet()) {
            if (s.contains(server)) servers.add(s);
        }
        return servers;
    }

}
