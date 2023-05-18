package fr.opaleuhc.opalevelocity.report;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.utils.DateUtils;
import fr.opaleuhc.opalevelocity.utils.WebHookUtils;
import net.kyori.adventure.text.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportCmd implements SimpleCommand {

    public HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("You must be a player to execute this command."));
            return;
        }
        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /report <joueur> <raison>"));
            return;
        }
        Player target = OpaleVelocity.INSTANCE.getProxy().getPlayer(args[0]).orElse(null);
        if (target == null) {
            source.sendMessage(Component.text("Ce joueur n'est pas connecté."));
            return;
        }
        if (cooldown.containsKey(p.getUniqueId())) {
            if (cooldown.get(p.getUniqueId()) > System.currentTimeMillis()) {
                source.sendMessage(Component.text("Vous devez attendre " + DateUtils.dateFromMillis(cooldown.get(p.getUniqueId())) + " " +
                        "avant de pouvoir effectuer un nouveau report."));
                return;
            }
        }
        cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 120000);
        String reason = String.join(" ", new ArrayList<>(List.of(args)).subList(1, args.length));
        CompletableFuture.runAsync(() -> {
            String serverName = (target.getCurrentServer().isEmpty()) ? "Aucun" : target.getCurrentServer().get().getServerInfo().getName();
            sendWebHook(p, args[0], reason, serverName);
            p.sendMessage(Component.text("§aReport envoyé !"));
        });
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return OpaleVelocity.INSTANCE.getEveryPlayersWithoutMe((Player) invocation.source());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }

    public void sendWebHook(Player p, String target, String reason, String server) {
        long start = System.currentTimeMillis();
        final WebHookUtils webhook = new WebHookUtils("https://discord.com/api/webhooks/1086949400191979560/5Zb3BtJVXtvisb98AHpahFg7sh14cwO7Cz_I7uQAHkt81F9eUCdL-tQe_I_KERmG144D");
        webhook.setAvatarUrl("https://opaleuhc.fr/favicon.ico");
        webhook.setUsername("OpaleVelocity - Report");
        webhook.addEmbed(new WebHookUtils.EmbedObject()
                .setDescription("**Auteur ·** ``" + p.getUsername() + "``\\n**Joueur signalé ·** ``" + target + "``\\n**Motif ·** ``" + reason + "``\\n**Serveur ·** ``" + server + "``")
                .setThumbnail("https://minotar.net/avatar/" + target + "/100.png")
                .setAuthor(p.getUsername(), "", "https://minotar.net/avatar/" + p.getUsername() + "/100.png")
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
