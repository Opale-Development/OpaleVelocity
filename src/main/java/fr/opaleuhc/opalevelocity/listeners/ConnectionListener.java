package fr.opaleuhc.opalevelocity.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import fr.opaleuhc.opalevelocity.maintenance.MaintenanceGManager;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanManager;
import fr.opaleuhc.opalevelocity.utils.User;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import net.kyori.adventure.text.Component;

public class ConnectionListener {

    @Subscribe
    public void onPreLogin(PreLoginEvent e) {

    }

    @Subscribe
    public void onLogin(LoginEvent e) {
        if (!MaintenanceGManager.instance.canJoin(e.getPlayer())) {
            e.setResult(ResultedEvent.ComponentResult.denied(Component.text("§cLe serveur est en maintenance.")));
        }
        User user = UserManager.instance.getAccount(e.getPlayer().getUniqueId(), e.getPlayer().getUsername());
        if (user.isBanned()) {
            if (user.getBanExpiration() != -1 && user.getBanExpiration() < System.currentTimeMillis()) {
                BanManager.instance.unban(user);
                return;
            }
            e.setResult(ResultedEvent.ComponentResult.denied(BanManager.instance.getDisconnectMessage(user.getBanReason(), user.getBanExpiration(), user.getBanAuthor())));
        }
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent e) {
        //ping
    }

}
