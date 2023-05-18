package fr.opaleuhc.opalevelocity.staff;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StaffGCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("§cYou must be a player to execute this command."));
            return;
        }
        String message = String.join(" ", args);
        OpaleVelocity.INSTANCE.sendStaffMsgToEveryoneOnTheProxy(message, "opaleuhc.staff", p.getUsername());
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
        return invocation.source().hasPermission("opaleuhc.staff");
    }

}
