package fr.opaleuhc.opalevelocity.sanctions.mute;


import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.utils.DateUtils;
import fr.opaleuhc.opalevelocity.utils.User;
import fr.opaleuhc.opalevelocity.utils.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MuteManager {

    public static MuteManager instance;

    public MuteManager() {
        instance = this;
    }

    public @NonNull ComponentLike getMutedMessage(String reason, long expiration, String muter) {
        return Component.text("§4§lVous êtes mute sur le serveur.\n\n§cRaison : §e" + reason + "\n§cExpiration : §e" +
                (expiration == -1 ? "Permanent" : DateUtils.dateFromMillis(expiration))
                + "\n§cPar : §e" + muter + "\n\nPour toute réclamation, merci de faire un ticket sur discord.\n§c§lhttps://discord.opaleuhc.fr");
    }

    public String mute(String targetStr, long duration, String reason, String muter, boolean doesBannerCanBypassExistingMute) {
        Player target = OpaleVelocity.instance.getProxy().getPlayer(targetStr).orElse(null);
        long expiration = (duration > 0 ? System.currentTimeMillis() + duration : -1);
        if (target != null) target.sendMessage(getMutedMessage(reason, expiration, muter));
        User user = UserManager.instance.getAccount(targetStr);
        if (user != null) {
            if (!user.isMuted()) {
                setMute(user, muter, reason, expiration);
                sendWebHook(muter, target, reason, duration, targetStr);
                return "muted";
            }
            if (doesBannerCanBypassExistingMute) {
                setMute(user, muter, reason, expiration);
                sendWebHook(muter, target, reason, duration, targetStr);
                return "overrided";
            }
            return "already muted";
        }
        return "no user";
    }

    public void sendWebHook(String muter, Player target, String reason, long duration, String targetName) {
        String serverName = "Aucun";
        if (target != null && target.getCurrentServer().isPresent())
            serverName = target.getCurrentServer().get().getServerInfo().getName();

        //SanctionWebhook.instance.sendWebHook(muter, targetName, reason, "Mute pendant " + DateUtils.getDurationIn(duration), serverName);
    }

    public void setMute(User user, String muter, String reason, long expiration) {
        user.setMute(muter, reason, expiration);
        UserManager.instance.makePatch(user.getUuid(), "mute", user.getMute());
    }

    public void unmute(User user) {
        user.setMute(null, null, 0);
        UserManager.instance.makePatch(user.getUuid(), "mute", "null");
        if (OpaleVelocity.instance.getProxy().getPlayer(user.getUuid()).isPresent())
            OpaleVelocity.instance.getProxy().getPlayer(user.getUuid()).get().sendMessage(getMessageOfEnd());
    }

    public Component getMessageOfEnd() {
        return Component.text("§cVotre mute est désormais terminé, nous rappelons par ailleurs, que vous devez respecter le règlement.");
    }

}
