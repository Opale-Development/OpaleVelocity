package fr.opaleuhc.opalevelocity.pm;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReplyCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("§cVous devez être connecté pour utiliser cette commande."));
            return;
        }
        if (args.length < 1) {
            source.sendMessage(Component.text("§cUsage: /r <message>"));
            return;
        }
        if (!MsgCmd.lastMsg.containsKey(p.getUniqueId())) {
            source.sendMessage(Component.text("§cVous n'avez pas encore envoyé de dm."));
            return;
        }
        Player target = OpaleVelocity.INSTANCE.getProxy().getPlayer(fr.opaleuhc.opalevelocity.pm.MsgCmd.lastMsg.get(p.getUniqueId())).orElse(null);
        if (target == null) {
            source.sendMessage(Component.text("§cCe joueur n'est pas connecté ou n'est plus connecté."));
            return;
        }
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        p.sendMessage(Component.text("§7[§3Moi -> " + target.getUsername() + "§7] §3" + message));
        target.sendMessage(Component.text("§7[§3" + p.getUsername() + " -> Moi§7] §3" + message));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }
}
