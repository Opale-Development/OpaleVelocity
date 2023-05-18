package fr.opaleuhc.opalevelocity.serverqueue;

import com.velocitypowered.api.proxy.Player;
import fr.opaleuhc.opalevelocity.serverstatus.ServerStatusManager;
import net.kyori.adventure.text.Component;

import java.util.*;

public class ServerQueueManager {

    public static ServerQueueManager INSTANCE;
    public HashMap<String, LinkedList<UUID>> queues = new HashMap<>();

    public ServerQueueManager() {
        INSTANCE = this;
    }

    public boolean isAlreadyInQueue(UUID uuid) {
        for (LinkedList<UUID> queue : queues.values()) {
            if (queue.contains(uuid)) return true;
        }
        return false;
    }

    public void joinQueue(String server, Player p) {
        ArrayList<String> serversSimilar = ServerStatusManager.INSTANCE.getServers(server);
        if (serversSimilar.size() == 0) {
            p.sendMessage(Component.text("§cAucun serveur ne correspond à votre recherche !"));
            return;
        }
        String chosenServer = serversSimilar.get(0);
        if (!queues.containsKey(chosenServer)) queues.put(chosenServer, new LinkedList<>());
        LinkedList<UUID> queue = queues.get(chosenServer);
        if (queue.contains(p.getUniqueId())) {
            p.sendMessage(Component.text("§cVous êtes déjà dans la file d'attente de ce serveur !"));
            return;
        }
        if (isAlreadyInQueue(p.getUniqueId())) {
            p.sendMessage(Component.text("§cVous êtes déjà dans une file d'attente !"));
            return;
        }
        queue.add(p.getUniqueId());
        p.sendMessage(Component.text("§aVous avez rejoint la file d'attente du serveur §e" + chosenServer + "§a."));
    }

    public void leaveQueues(Player p) {
        for (Map.Entry<String, LinkedList<UUID>> entry : queues.entrySet()) {
            LinkedList<UUID> queue = entry.getValue();
            if (queue != null) {
                if (queue.contains(p.getUniqueId())) {
                    queue.remove(p.getUniqueId());
                    p.sendMessage(Component.text("§aVous avez quitté la file d'attente du serveur §e" + entry.getKey() + "§a."));
                }
            }
        }
    }

    public void queueUpdate() {

    }
}
