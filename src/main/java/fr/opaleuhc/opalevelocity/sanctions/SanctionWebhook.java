package fr.opaleuhc.opalevelocity.sanctions;

import fr.opaleuhc.opalevelocity.utils.DateUtils;
import fr.opaleuhc.opalevelocity.utils.WebHookUtils;

import java.awt.*;
import java.io.IOException;

public class SanctionWebhook {

    private static final String webhook_url = "https://discord.com/api/webhooks/1093175865132720198/_A5H1VDkh4rnwhTVu9v5rBTVjLxOzPmjDCLzkRfx0AF7FW5FVdb-LjI0GOqc5F8ST6KF";
    public static SanctionWebhook INSTANCE;

    public SanctionWebhook() {
        INSTANCE = this;
    }

    public void sendWebHook(String p, String target, String reason, String sanction, String server) {
        long start = System.currentTimeMillis();
        final WebHookUtils webhook = new WebHookUtils(webhook_url);
        webhook.setAvatarUrl("https://opaleuhc.fr/favicon.ico");
        webhook.setUsername("OpaleVelocity - Sanctions");
        webhook.addEmbed(new WebHookUtils.EmbedObject()
                .setDescription("**Auteur ·** ``" + p + "``\\n**Joueur sanctionné ·** ``" + target + "``\\n**Motif ·** ``" +
                        reason + "``\\n**Sanction ·** ``" + sanction + "``\\n**Serveur ·** ``" + server + "``")
                .setThumbnail("https://minotar.net/avatar/" + target + "/100.png")
                .setAuthor(p, "", "https://minotar.net/avatar/" + p + "/100.png")
                .setFooter("Envoyé depuis OpaleVelocity à " + DateUtils.dateFromMillis(System.currentTimeMillis()), "https://opaleuhc.fr/favicon.ico")
                .setColor(Color.decode("#29ad91")));
        webhook.setTts(false);
        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sended report in " + (System.currentTimeMillis() - start) + "ms");
    }
}
