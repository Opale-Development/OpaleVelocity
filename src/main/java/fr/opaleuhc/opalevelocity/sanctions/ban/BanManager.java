package fr.opaleuhc.opalevelocity.sanctions.ban;


import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.sanctions.SanctionWebhook;
import fr.opaleuhc.opalevelocity.utils.DateUtils;
import fr.opaleuhc.opalevelocity.utils.User;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import net.kyori.adventure.text.Component;

public class BanManager {

    public static BanManager INSTANCE;

    public BanManager() {
        INSTANCE = this;
    }

    public void disconnect(Player target, String reason, long expiration, String banner) {
        target.disconnect(getDisconnectMessage(reason, expiration, banner));
    }

    public Component getDisconnectMessage(String reason, long expiration, String banner) {
        return Component.text("§4§lVous êtes banni du serveur.\n\n§cRaison : §e" + reason + "\n§cExpiration : §e" +
                (expiration == -1 ? "Permanent" : DateUtils.dateFromMillis(expiration))
                + "\n§cPar : §e" + banner + "\n\nPour toute réclamation, merci de faire un ticket sur discord.\n§c§lhttps://discord.opaleuhc.fr");
    }

    public String ban(String targetStr, long duration, String reason, String banner, boolean doesBannerCanBypassExistingBan) {
        Player target = OpaleVelocity.INSTANCE.getProxy().getPlayer(targetStr).orElse(null);
        long expiration = (duration > 0 ? System.currentTimeMillis() + duration : -1);
        if (target != null) disconnect(target, reason, expiration, banner);
        User user = UserManager.INSTANCE.getAccount(targetStr);
        if (user != null) {
            if (!user.isBanned()) {
                setBan(user, banner, reason, expiration);
                sendWebHook(banner, target, reason, duration, targetStr);
                return "banned";
            }
            if (doesBannerCanBypassExistingBan) {
                setBan(user, banner, reason, expiration);
                sendWebHook(banner, target, reason, duration, targetStr);
                return "overrided";
            }
            return "already banned";
        }
        return "no user";
    }

    public void sendWebHook(String muter, Player target, String reason, long duration, String targetName) {
        String serverName = "Aucun";
        if (target != null && target.getCurrentServer().isPresent())
            serverName = target.getCurrentServer().get().getServerInfo().getName();

        SanctionWebhook.INSTANCE.sendWebHook(muter, targetName, reason, "Ban pendant " + DateUtils.getDurationIn(duration), serverName);
    }

    public void setBan(User user, String banner, String reason, long expiration) {
        user.setBan(banner, reason, expiration);
        UserManager.INSTANCE.makePatch(user.getUuid(), "ban", user.getBan());
    }

    public void unban(User user) {
        user.setBan(null, null, 0);
        UserManager.INSTANCE.makePatch(user.getUuid(), "ban", "null");
        if (OpaleVelocity.INSTANCE.getProxy().getPlayer(user.getUuid()).isPresent())
            OpaleVelocity.INSTANCE.getProxy().getPlayer(user.getUuid()).get().sendMessage(Component.text("§aVous avez été débanni du serveur."));
    }
}
