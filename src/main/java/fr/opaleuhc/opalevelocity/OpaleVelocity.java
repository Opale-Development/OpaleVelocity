package fr.opaleuhc.opalevelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import fr.opaleuhc.opalevelocity.cmd.*;
import fr.opaleuhc.opalevelocity.cpm.CPMListener;
import fr.opaleuhc.opalevelocity.dependencies.LuckPerms;
import fr.opaleuhc.opalevelocity.listeners.ConnectionListener;
import fr.opaleuhc.opalevelocity.listeners.PlayerListener;
import fr.opaleuhc.opalevelocity.maintenance.MaintenanceGCmd;
import fr.opaleuhc.opalevelocity.maintenance.MaintenanceGManager;
import fr.opaleuhc.opalevelocity.pm.MsgCmd;
import fr.opaleuhc.opalevelocity.pm.ReplyCmd;
import fr.opaleuhc.opalevelocity.report.ReportCmd;
import fr.opaleuhc.opalevelocity.sanctions.SanctionWebhook;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanCmd;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanManager;
import fr.opaleuhc.opalevelocity.sanctions.mute.MuteCmd;
import fr.opaleuhc.opalevelocity.sanctions.mute.MuteManager;
import fr.opaleuhc.opalevelocity.serverstatus.ServerStatusManager;
import fr.opaleuhc.opalevelocity.staff.StaffCmd;
import fr.opaleuhc.opalevelocity.staff.StaffGCmd;
import fr.opaleuhc.opalevelocity.tab.TABManager;
import fr.opaleuhc.opalevelocity.utils.HTTPUtils;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Plugin(
        id = "opalevelocity",
        name = "OpaleVelocity",
        version = "1.0.0",
        description = "OpaleVelocity plugin",
        url = "https://opaleuhc.fr",
        authors = "OpaleUHC",
        dependencies = {
                @Dependency(id = "luckperms")
        }
)
public class OpaleVelocity {

    public static OpaleVelocity INSTANCE;
    private final ProxyServer proxy;
    private final Logger logger;

    public String serverMotd = "§3§lOpale - 0.0.1 §7- §1Ouvert\n§bFaction, LG UHC, Mario UHC et bientôt plus..";

    @Inject
    public OpaleVelocity(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        INSTANCE = this;

        logger.info("Loaded velocity started, awaiting for proxy initialization...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        logger.info("Proxy initialized, loading OpaleVelocity...");

        HTTPUtils.setApiKey(System.getenv("API_KEY"));

        logger.info("Registering dependencies...");
        new LuckPerms(proxy, logger, LuckPermsProvider.get());

        logger.info("Initializing managers...");
        new UserManager();
        new BanManager();
        new MuteManager();
        new TABManager();
        new MaintenanceGManager();
        new SanctionWebhook();
        new ServerStatusManager();

        logger.info("Registering listeners...");
        proxy.getEventManager().register(this, new PlayerListener());
        proxy.getEventManager().register(this, new ConnectionListener());
        proxy.getEventManager().register(this, new CPMListener(proxy, logger));

        logger.info("Registering commands...");
        proxy.getCommandManager().register("b", new BanCmd(), "ban");
        proxy.getCommandManager().register("m", new MuteCmd(), "mute");
        proxy.getCommandManager().register("msg", new MsgCmd());
        proxy.getCommandManager().register("r", new ReplyCmd(), "reply");
        proxy.getCommandManager().register("report", new ReportCmd());
        proxy.getCommandManager().register("discord", new DiscordCmd());
        proxy.getCommandManager().register("mumble", new MumbleCmd());
        proxy.getCommandManager().register("twitter", new TwitterCmd());
        proxy.getCommandManager().register("staff", new StaffCmd());
        proxy.getCommandManager().register("staffg", new StaffGCmd());
        proxy.getCommandManager().register("info", new InfoCmd());
        proxy.getCommandManager().register("infog", new InfoGCmd());
        proxy.getCommandManager().register("hub", new HubCmd());
        proxy.getCommandManager().register("maintenanceg", new MaintenanceGCmd());

        logger.info("OpaleVelocity loaded!");
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public RegisteredServer getHubServer() {
        for (RegisteredServer rs : getProxy().getAllServers()) {
            if (rs.getServerInfo().getName().contains("hub")) return rs;

        }
        return null;
    }

    public CompletableFuture<List<String>> getEveryPlayersWithoutMe(Player p) {
        ArrayList<String> players = new ArrayList<>();
        for (Player player : proxy.getAllPlayers()) {
            if (!player.equals(p)) players.add(player.getUsername());
        }
        return CompletableFuture.completedFuture(players);
    }

    public void sendStaffMsgToEveryoneOnTheSpigot(String message, String perm, ServerInfo serverInfo, String sender) {
        for (Player player : proxy.getAllPlayers()) {
            if (!player.hasPermission(perm)) continue;
            if (serverInfo != null && player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServer().getServerInfo().equals(serverInfo)) {
                player.sendMessage(Component.text("§c[STAFF : " + sender + "] §f" + message));
            }
        }
    }

    public void sendStaffMsgToEveryoneOnTheProxy(String message, String perm, String sender) {
        for (Player p : proxy.getAllPlayers()) {
            if (!p.hasPermission(perm)) continue;
            p.sendMessage(Component.text("§c[STAFF GLOBAL : " + sender + "] §f" + message));
        }
    }

    public void sendToEveryoneOnSpigot(String message, ServerInfo serverInfo) {
        for (Player player : proxy.getAllPlayers()) {
            if (serverInfo != null && player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServer().getServerInfo().equals(serverInfo)) {
                player.sendMessage(Component.text(message));
            }
        }
    }

    public void sendToEveryoneOnProxy(String message) {
        for (Player p : proxy.getAllPlayers()) {
            p.sendMessage(Component.text(message));
        }
    }

    public boolean canUpdateTABForServer(ServerInfo serverInfo) {
        return serverInfo.getName().contains("hub") || serverInfo.getName().contains("lobby");
    }
}
