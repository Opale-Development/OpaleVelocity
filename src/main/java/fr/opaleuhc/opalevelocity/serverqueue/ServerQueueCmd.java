package fr.opaleuhc.opalevelocity.serverqueue;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.serverstatus.ServerStatusManager;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerQueueCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("§cVous devez être un joueur pour exécuter cette commande !"));
            return;
        }

        if (invocation.arguments().length != 2) {
            if (invocation.arguments().length == 1 && invocation.arguments()[0].equalsIgnoreCase("leave")) {
                ServerQueueManager.INSTANCE.leaveQueues(p);
                return;
            }
            p.sendMessage(Component.text("§cUtilisation: /sq <join> <serveur> OU /sq leave"));
            return;
        }
        String action = invocation.arguments()[0];
        String server = invocation.arguments()[1];
        if (!action.equalsIgnoreCase("join")) {
            p.sendMessage(Component.text("§cUtilisation: /sq <join> <serveur> OU /sq leave"));
            return;
        }
        ServerQueueManager.INSTANCE.joinQueue(server, p);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        if (invocation.arguments().length == 1) {
            suggestions.add("join");
            suggestions.add("leave");
        } else if (invocation.arguments().length == 2) {
            if (invocation.arguments()[0].equalsIgnoreCase("leave"))
                return CompletableFuture.completedFuture(suggestions);
            suggestions.addAll(ServerStatusManager.INSTANCE.getServers(invocation.arguments()[1]));
        }
        return CompletableFuture.completedFuture(suggestions);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }
}
