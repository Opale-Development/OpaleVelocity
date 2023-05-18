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

        if (!source.hasPermission("opale.maintenanceg.cmd")) {
            source.sendMessage(Component.text("§cVous n'avez pas la permission d'executer cette commande !"));
            return;
        }
        boolean oldMaintenance = MaintenanceGManager.INSTANCE.isMaintenance();
        source.sendMessage(Component.text("§aMaintenance avant la commande : §e" + (oldMaintenance ? "§cON" : "§aOFF") + "§a."));
        if (args.length == 1) {
            boolean maintenance = args[0].equalsIgnoreCase("on");
            boolean maintenance2 = args[0].equalsIgnoreCase("off");
            if (maintenance) {
                if (oldMaintenance) {
                    source.sendMessage(Component.text("§cLe serveur est déjà en maintenance."));
                    return;
                }
                MaintenanceGManager.INSTANCE.setMaintenance(true);
                source.sendMessage(Component.text("§aMaintenance : §cON"));
                return;
            }
            if (maintenance2) {
                if (!oldMaintenance) {
                    source.sendMessage(Component.text("§cLe serveur n'était pas en maintenance."));
                    return;
                }
                MaintenanceGManager.INSTANCE.setMaintenance(false);
                source.sendMessage(Component.text("§aMaintenance : §aOFF"));
                return;
            }
        }
        source.sendMessage(Component.text("§cUsage: /maintenanceg <on/off>"));
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
        return invocation.source().hasPermission("opale.maintenanceg.cmd");
    }
}
