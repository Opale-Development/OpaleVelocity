package fr.opaleuhc.opalevelocity.pm;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MsgCmd implements SimpleCommand {

    public static HashMap<UUID, UUID> lastMsg = new HashMap<>();

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("§cVous devez être connecté pour utiliser cette commande."));
            return;
        }
        if (args.length < 2) {
            source.sendMessage(Component.text("§cUsage: /msg <player> <message>"));
            return;
        }
        Player target = OpaleVelocity.instance.getProxy().getPlayer(args[0]).orElse(null);
        if (target == null) {
            source.sendMessage(Component.text("§cCe joueur n'est pas connecté."));
            return;
        }
        if (p.equals(target)) {
            source.sendMessage(Component.text("§cVous ne pouvez pas vous envoyer de message à vous-même."));
            return;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        lastMsg.put(target.getUniqueId(), p.getUniqueId());
        lastMsg.put(p.getUniqueId(), target.getUniqueId());
        p.sendMessage(Component.text("§7[§3Moi -> " + target.getUsername() + "§7] §3" + message));
        target.sendMessage(Component.text("§7[§3" + p.getUsername() + " -> Moi§7] §3" + message));
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
        return SimpleCommand.super.hasPermission(invocation);
    }
}
