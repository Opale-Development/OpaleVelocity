package fr.opaleuhc.opalevelocity.staff;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StaffCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("§cYou must be a player to execute this command."));
            return;
        }
        if (p.getCurrentServer().isEmpty()) {
            p.sendMessage(Component.text("§cVous devez être connecté sur un serveur."));
            return;
        }
        String message = String.join(" ", args);
        OpaleVelocity.INSTANCE.sendStaffMsgToEveryoneOnTheSpigot(message, "opaleuhc.staff", p.getCurrentServer().get().getServerInfo(), p.getUsername());
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
