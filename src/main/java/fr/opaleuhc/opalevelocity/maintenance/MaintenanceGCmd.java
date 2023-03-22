package fr.opaleuhc.opalevelocity.maintenance;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MaintenanceGCmd implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("opaleuhc.maintenanceg.cmd")) {
            source.sendMessage(Component.text("§cVous n'avez pas la permission d'executer cette commande !"));
            return;
        }
        if (args.length != 0) {
            source.sendMessage(Component.text("§cUsage: /maintenanceg <on/off>"));
            return;
        }
        MaintenanceGManager.instance.setMaintenance(!MaintenanceGManager.instance.isMaintenance());
        source.sendMessage(Component.text("§aMaintenance: " + (MaintenanceGManager.instance.isMaintenance() ? "§cON" : "§aOFF")));
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
