package fr.opaleuhc.opalevelocity.sanctions.ban;


import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.utils.DateUtils;
import fr.opaleuhc.opalevelocity.utils.User;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class BanManager {

    @Getter
    public static BanManager instance;

    public BanManager() {
        instance = this;
    }

    public void disconnect(Player target, String reason, long expiration, String banner) {
        target.disconnect(getDisconnectMessage(reason, expiration, banner));
    }

    public Component getDisconnectMessage(String reason, long expiration, String banner) {
        return Component.text("§4§lVous êtes banni du serveur.\n\n§cRaison : §e" + reason + "\n§cExpiration : §e" + (expiration == -1 ? "Permanent" : DateUtils.dateFromMillis(expiration))
                + "\n§cPar : §e" + banner);
    }

    public String ban(String targetStr, long duration, String reason, String banner, boolean doesBannerCanBypassExistingBan) {
        Player target = OpaleVelocity.getInstance().getProxy().getPlayer(targetStr).orElse(null);
        long expiration = (duration > 0 ? System.currentTimeMillis() + duration : -1);
        if (target != null) disconnect(target, reason, expiration, banner);
        User user = UserManager.getInstance().getAccount(targetStr);
        if (user != null) {
            if (!user.isBanned()) {
                setBan(user, banner, reason, expiration);
                return "banned";
            }
            if (doesBannerCanBypassExistingBan) {
                setBan(user, banner, reason, expiration);
                return "overrided";
            }
            return "already banned";
        }
        return "no user";
    }

    public void setBan(User user, String banner, String reason, long expiration) {
        user.setBan(banner, reason, expiration);
        UserManager.getInstance().makePatch(user.getUuid(), "ban", user.getBan());
    }

    public void unban(User user) {
        user.setBan(null, null, 0);
        UserManager.getInstance().makePatch(user.getUuid(), "ban", "null");
        if (OpaleVelocity.getInstance().getProxy().getPlayer(user.getUuid()).isPresent())
            OpaleVelocity.getInstance().getProxy().getPlayer(user.getUuid()).get().sendMessage(Component.text("§aVous avez été débanni du serveur."));
    }
}