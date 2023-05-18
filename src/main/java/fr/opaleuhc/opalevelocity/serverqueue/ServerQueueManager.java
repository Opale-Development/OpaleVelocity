package fr.opaleuhc.opalevelocity.serverqueue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.serverstatus.ServerStatus;
import fr.opaleuhc.opalevelocity.serverstatus.ServerStatusManager;
import net.kyori.adventure.text.Component;

import java.util.*;

public class ServerQueueManager {

    public static ServerQueueManager INSTANCE;
    public HashMap<String, LinkedList<UUID>> queues = new HashMap<>();

    public ServerQueueManager() {
        INSTANCE = this;

        OpaleVelocity.instance.getProxy().getScheduler().buildTask(OpaleVelocity.instance, this::queueTick).repeat(3, java.util.concurrent.TimeUnit.SECONDS).schedule();
        OpaleVelocity.instance.getProxy().getScheduler().buildTask(OpaleVelocity.instance, this::queueUpdate).repeat(1, java.util.concurrent.TimeUnit.SECONDS).schedule();
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
        Optional<ServerConnection> sc = p.getCurrentServer();
        if (sc.isPresent()) {
            if (sc.get().getServer().getServerInfo().getName().equalsIgnoreCase(chosenServer)) {
                p.sendMessage(Component.text("§cVous êtes déjà connecté à ce serveur !"));
                return;
            }
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
        //this method check if players are still in the queue and remove them if they are not and send them an action bar message of their position in the queue
        for (Map.Entry<String, LinkedList<UUID>> entry : queues.entrySet()) {
            LinkedList<UUID> queue = entry.getValue();
            if (queue == null) continue;
            if (queue.size() == 0) continue;
            ArrayList<UUID> toRemove = new ArrayList<>();
            for (UUID uuid : queue) {
                Player p = OpaleVelocity.instance.getProxy().getPlayer(uuid).orElse(null);
                if (p == null) {
                    toRemove.add(uuid);
                    continue;
                }
                Optional<ServerConnection> server = p.getCurrentServer();
                if (server.isPresent()) {
                    if (server.get().getServer().getServerInfo().getName().equalsIgnoreCase(entry.getKey())) {
                        toRemove.add(uuid);
                        p.sendMessage(Component.text("§cErreur interne, vous êtes déjà sur le serveur demandé."));
                    }
                }
            }
            queue.removeAll(toRemove);
            for (int i = 0; i < queue.size(); i++) {
                UUID uuid = queue.get(i);
                Player p = OpaleVelocity.instance.getProxy().getPlayer(uuid).orElse(null);
                if (p == null) continue;
                p.sendActionBar(Component.text("§aServeur : §e" + entry.getKey() + " " + ServerStatusManager.INSTANCE.getServerStatusString(entry.getKey()) +
                        "§a, §e" + (i + 1) + "§8/§e" + queue.size() + "§a dans la file d'attente."));
            }
        }
    }

    public void queueTick() {
        //this method process the player to the servers
        queueUpdate();
        for (Map.Entry<String, LinkedList<UUID>> entry : queues.entrySet()) {
            LinkedList<UUID> queue = entry.getValue();
            if (queue == null) continue;
            if (queue.size() == 0) continue;
            Player p = OpaleVelocity.instance.getProxy().getPlayer(queue.get(0)).orElse(null);
            if (p == null) continue;
            if (ServerStatusManager.INSTANCE.getServerStatus(entry.getKey()) == ServerStatus.ON) {

                //TODO: maintenance par serveur

                p.createConnectionRequest(OpaleVelocity.instance.getProxy().getServer(entry.getKey()).get()).connect().thenAccept((v) -> {
                    if (v.isSuccessful()) {
                        queue.remove(p.getUniqueId());
                        p.sendMessage(Component.text("§aVous avez été connecté au serveur §e" + entry.getKey() + "§a avec succès."));
                        p.sendActionBar(Component.text("§aConnexion réussie !"));
                    } else {
                        v.getReasonComponent().ifPresent((r) -> p.sendMessage(Component.text("§cUne erreur est survenue lors de la connexion au serveur §e" + entry.getKey() + "§c : " + r)));
                    }
                });
            }
        }
    }
}
