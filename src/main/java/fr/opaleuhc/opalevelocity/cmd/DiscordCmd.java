package fr.opaleuhc.opalevelocity.cmd;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiscordCmd implements SimpleCommand {

    public static void sendDiscord(Player p) {
        Component component = Component.text("\n§3Discord : §bhttps://discord.opaleuhc.fr\n");
        component = component.hoverEvent(Component.text("§bCliquez ici pour rejoindre le discord !"));
        component = component.clickEvent(ClickEvent.openUrl("https://discord.opaleuhc.fr"));
        p.sendMessage(component);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player p)) {
            source.sendMessage(Component.text("§cVous devez être un joueur pour exécuter cette commande !"));
            return;
        }
        sendDiscord(p);
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
