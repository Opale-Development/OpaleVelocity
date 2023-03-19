package fr.opaleuhc.opalevelocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.sanctions.mute.MuteManager;
import fr.opaleuhc.opalevelocity.utils.User;
import fr.opaleuhc.opalevelocity.utils.UserManager;

public class PlayerListener {

    @Subscribe
    public void onPlayerChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        User user = UserManager.getInstance().getAccount(p.getUniqueId(), p.getUsername());
        if (user == null) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
            return;
        }
        if (user.isMuted()) {
            if (user.getMuteExpiration() != -1 && user.getMuteExpiration() < System.currentTimeMillis()) {
                MuteManager.getInstance().unmute(user);
                return;
            }
            e.setResult(PlayerChatEvent.ChatResult.denied());
            p.sendMessage(MuteManager.getInstance().getMutedMessage(user.getMuteReason(), user.getMuteExpiration(), user.getMuteAuthor()));
        }
    }
}
