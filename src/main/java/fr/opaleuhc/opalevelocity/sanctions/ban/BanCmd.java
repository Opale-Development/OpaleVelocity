package fr.opaleuhc.opalevelocity.sanctions.ban;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.utils.DateUtils;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BanCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sender.sendMessage(Component.text("§c[§e§l✯§c]§r » Usage §e/ban <joueur> <duration(1 + s/m/h/d/w/M/y)> <raison>"));
            return;
        }
        CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            sender.sendMessage(Component.text("§c[§e§l✯§c]§r » Vérification de la cible '§e" + args[0] + "§r' ..."));
            String r = "Non spécifiée";
            long duration = -1;
            if (args.length > 1) {
                try {
                    duration = DateUtils.getDurationFromStr(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(Component.text("§c[§e§l✯§c]§r » La durée spécifiée est incorrecte !"));
                    return;
                }
            }
            if (args.length > 2) {
                if (duration == 0) {
                    sender.sendMessage(Component.text("§c[§e§l✯§c]§r » La durée spécifiée est incorrecte !"));
                    return;
                }
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                r = reason.toString();
            }
            boolean canBypassExistingBan = sender.hasPermission("opale.bypass.otherban");
            String senderName = (sender instanceof Player p) ? p.getUsername() : "Console";
            String result = BanManager.instance.ban(args[0], duration, r, senderName, canBypassExistingBan);
            if (result.equalsIgnoreCase("banned"))
                sender.sendMessage(Component.text("§c[§e§l✯§c]§r » Le joueur §e" + args[0] + "§r a été banni jusqu'au/de façon §e" + DateUtils.duration(duration)
                        + "§r pour la raison suivante: §e" + r + " §r(§e" + (System.currentTimeMillis() - start) + "ms§r)"));
            else if (result.equalsIgnoreCase("overrided"))
                sender.sendMessage(Component.text("§c[§e§l✯§c]§r » Le joueur §e" + args[0] + "§r a été RE-banni par dessus une ancienne sanction, jusqu'au/de façon §e" + fr.opaleuhc.opalevelocity.utils.DateUtils.duration(duration)
                        + "§r pour la raison suivante: §e" + r + " §r(§e" + (System.currentTimeMillis() - start) + "ms§r)"));
            else if (result.equalsIgnoreCase("already banned"))
                sender.sendMessage(Component.text("§c[§e§l✯§c]§r » Le joueur §e" + args[0] + "§r est déjà banni !" + " §r(§e" + (System.currentTimeMillis() - start) + "ms§r)"));
            else
                sender.sendMessage(Component.text("§c[§e§l✯§c]§r » Le joueur §e" + args[0] + "§r n'a pas été banni car il n'existe pas !" + " §r(§e" + (System.currentTimeMillis() - start) + "ms§r)"));
        });
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return OpaleVelocity.instance.getEveryPlayersWithoutMe((Player) invocation.source());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("opale.ban");
    }
}
