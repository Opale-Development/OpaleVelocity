package fr.opaleuhc.opalevelocity.maintenance;

import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import net.kyori.adventure.text.Component;

public class MaintenanceGManager {

    public static MaintenanceGManager instance;
    public boolean maintenance = true;
    public String maintenanceMessage = "§3§lOpale §7- §c§lMAINTENANCE EN COURS...\n§a>>> §dhttps://discord.opaleuhc.fr";

    public MaintenanceGManager() {
        instance = this;
    }

    public boolean isMaintenance() {
        return this.maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;

        checkForEvacuation();
    }

    public boolean canJoin(Player p) {
        if (maintenance) {
            return p.hasPermission("opaleuhc.maintenanceg.bypass");
        }
        return true;
    }

    public void checkForEvacuation() {
        for (Player p : OpaleVelocity.instance.getProxy().getAllPlayers()) {
            if (!canJoin(p)) {
                p.disconnect(Component.text("§cLe serveur est en maintenance."));
            }
        }
    }
}
