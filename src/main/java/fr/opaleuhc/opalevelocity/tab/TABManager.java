package fr.opaleuhc.opalevelocity.tab;

import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TABManager {

    public static TABManager INSTANCE;

    public TABManager() {
        INSTANCE = this;

        OpaleVelocity.INSTANCE.getProxy().getScheduler().buildTask(OpaleVelocity.INSTANCE, this::clock).repeat(1, TimeUnit.SECONDS).schedule();
    }

    public void clock() {
        CompletableFuture.runAsync(() -> {
            int global_connected = OpaleVelocity.INSTANCE.getProxy().getAllPlayers().size();
            for (Player player : OpaleVelocity.INSTANCE.getProxy().getAllPlayers()) {
                if (player.getCurrentServer().isPresent() && OpaleVelocity.INSTANCE.canUpdateTABForServer(player.getCurrentServer().get().getServerInfo()))
                    player.getTabList().setHeaderAndFooter(
                            Component.text("§3§lOpaleUHC\n\n§7Connectés : §f" + global_connected + "\n"),
                            Component.text("\n§3mc.opaleuhc.fr")
                    );
                /*for (TabListEntry entry : player.getTabList().getEntries()) {
                    String prefix = LuckPerms.INSTANCE.getPrefix(entry.getProfile().getId());
                    entry.setDisplayName(Component.text((prefix.length() > 1 ? prefix + " " : "") + "§f" + entry.getProfile().getName()));
                }*/
            }
        });
    }

}
