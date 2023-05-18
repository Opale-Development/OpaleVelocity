package fr.opaleuhc.opalevelocity.cmd;

import com.velocitypowered.api.command.SimpleCommand;
import fr.opaleuhc.opalevelocity.OpaleVelocity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InfoGCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        String message = "§6[Info-Global] §f" + String.join(" ", args);
        OpaleVelocity.INSTANCE.sendToEveryoneOnProxy(message);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return new CompletableFuture<>();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("opaleuhc.info");
    }

}
