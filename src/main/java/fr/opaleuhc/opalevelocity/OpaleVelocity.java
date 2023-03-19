package fr.opaleuhc.opalevelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.opaleuhc.opalevelocity.listeners.ConnectionListener;
import fr.opaleuhc.opalevelocity.listeners.PlayerListener;
import fr.opaleuhc.opalevelocity.pm.MsgCmd;
import fr.opaleuhc.opalevelocity.pm.ReplyCmd;
import fr.opaleuhc.opalevelocity.report.ReportCmd;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanCmd;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanManager;
import fr.opaleuhc.opalevelocity.sanctions.mute.MuteCmd;
import fr.opaleuhc.opalevelocity.sanctions.mute.MuteManager;
import fr.opaleuhc.opalevelocity.utils.HTTPUtils;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import lombok.Getter;
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
        authors = "OpaleUHC"
)
public class OpaleVelocity {

    @Getter
    public static OpaleVelocity instance;
    @Getter
    private final ProxyServer proxy;
    @Getter
    private final Logger logger;

    @Inject
    public OpaleVelocity(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        instance = this;

        logger.info("Loaded velocity plugin, awaiting for proxy initialization...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        logger.info("Proxy initialized, loading OpaleVelocity...");

        HTTPUtils.setApiKey(System.getenv("API_KEY"));

        logger.info("Initializing managers...");
        new UserManager();
        new BanManager();
        new MuteManager();

        logger.info("Registering listeners...");
        proxy.getEventManager().register(this, new PlayerListener());
        proxy.getEventManager().register(this, new ConnectionListener());

        logger.info("Registering commands...");
        proxy.getCommandManager().register("b", new BanCmd(), "ban");
        proxy.getCommandManager().register("m", new MuteCmd(), "mute");
        proxy.getCommandManager().register("msg", new MsgCmd());
        proxy.getCommandManager().register("r", new ReplyCmd(), "reply");
        proxy.getCommandManager().register("report", new ReportCmd());

        logger.info("OpaleVelocity loaded!");
    }

    public CompletableFuture<List<String>> getEveryPlayersWithoutMe(Player p) {
        ArrayList<String> players = new ArrayList<>();
        for (Player player : proxy.getAllPlayers()) {
            if (!player.equals(p)) players.add(player.getUsername());
        }
        return CompletableFuture.completedFuture(players);
    }
}