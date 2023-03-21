package fr.opaleuhc.opalevelocity.cmd;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HubCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("You must be a player to execute this command"));
            return;
        }
        Optional<ServerConnection> hub = p.getCurrentServer();
        if (hub.isPresent()) {
            ServerInfo hubInfo = hub.get().getServerInfo();
            if (hubInfo.getName().contains("hub")) {
                p.sendMessage(Component.text("§cVous êtes déjà sur un hub !"));
                return;
            }
            RegisteredServer hubServer = OpaleVelocity.instance.getHubServer();
            if (hubServer == null) {
                p.sendMessage(Component.text("§cAucun hub n'a été trouvé !"));
                return;
            }
            p.createConnectionRequest(hubServer).fireAndForget();
            p.sendMessage(Component.text("§aTentative de connexion au hub..."));
        }
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
