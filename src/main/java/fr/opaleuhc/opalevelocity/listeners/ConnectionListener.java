package fr.opaleuhc.opalevelocity.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.maintenance.MaintenanceGManager;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanManager;
import fr.opaleuhc.opalevelocity.utils.User;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.UUID;

public class ConnectionListener {

    @Subscribe
    public void onPreLogin(PreLoginEvent e) {

    }

    @Subscribe
    public void onLogin(LoginEvent e) {
        if (!MaintenanceGManager.INSTANCE.canJoin(e.getPlayer())) {
            e.setResult(ResultedEvent.ComponentResult.denied(Component.text("§cLe serveur est en maintenance.")));
            return;
        }
        User user = UserManager.INSTANCE.getAccount(e.getPlayer().getUniqueId(), e.getPlayer().getUsername());
        if (user.isBanned()) {
            if (user.getBanExpiration() != -1 && user.getBanExpiration() < System.currentTimeMillis()) {
                BanManager.INSTANCE.unban(user);
                return;
            }
            e.setResult(ResultedEvent.ComponentResult.denied(BanManager.INSTANCE.getDisconnectMessage(user.getBanReason(), user.getBanExpiration(), user.getBanAuthor())));
            return;
        }
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent e) {
        Component description;
        ServerPing.Players players;
        ServerPing.Version version;
        ArrayList<ServerPing.SamplePlayer> samplePlayers = new ArrayList<>();
        samplePlayers.add(new ServerPing.SamplePlayer("§dOn revient au plus vite :D", UUID.fromString("00000000-0000-0000-0000-000000000000")));
        if (!MaintenanceGManager.INSTANCE.isMaintenance()) {
            description = Component.text(OpaleVelocity.INSTANCE.serverMotd);
            players = new ServerPing.Players(OpaleVelocity.INSTANCE.getProxy().getPlayerCount(), 250, samplePlayers);
            version = new ServerPing.Version(762, "Opale est en 1.19.4+");
        } else {
            description = Component.text(MaintenanceGManager.INSTANCE.maintenanceMessage);
            players = new ServerPing.Players(OpaleVelocity.INSTANCE.getProxy().getPlayerCount(), 0, samplePlayers);
            version = new ServerPing.Version(9999, "§4§lMaintenance...");
        }
        ServerPing ping = new ServerPing(version, players, description, e.getPing().getFavicon().orElse(null), e.getPing().getModinfo().orElse(null));
        e.setPing(ping);
    }

}
