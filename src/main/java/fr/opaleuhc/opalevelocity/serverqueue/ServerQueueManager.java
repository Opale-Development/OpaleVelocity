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
    public HashMap<String, Integer> skipTicks = new HashMap<>();
    public HashMap<UUID, ServerQueuePriority> priority = new HashMap<>();

    public ServerQueueManager() {
        INSTANCE = this;

        OpaleVelocity.INSTANCE.getProxy().getScheduler().buildTask(OpaleVelocity.INSTANCE, this::queueTick).repeat(3, java.util.concurrent.TimeUnit.SECONDS).schedule();
        OpaleVelocity.INSTANCE.getProxy().getScheduler().buildTask(OpaleVelocity.INSTANCE, this::queueUpdate).repeat(1, java.util.concurrent.TimeUnit.SECONDS).schedule();
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
        ServerQueuePriority priority = getPriority(p);
        addToQueueWithPriority(priority, p, queue);
        p.sendMessage(Component.text("§aVous avez rejoint la file d'attente du serveur §e" + chosenServer + "§a. Priorité : " + priority.getName() + "§a."));
    }

    public ServerQueuePriority getPriority(Player p) {
        if (p.hasPermission("opale.staff")) return ServerQueuePriority.PRIORITAIRE;
        if (p.hasPermission("opale.haute")) return ServerQueuePriority.HAUTE;
        if (p.hasPermission("opale.normale")) return ServerQueuePriority.NORMALE;
        return ServerQueuePriority.BASSE;
    }

    public void addToQueueWithPriority(ServerQueuePriority sqp, Player p, LinkedList<UUID> queue) {
        int i = 0;
        for (UUID uuid : queue) {
            if (priority.containsKey(uuid)) {
                if (priority.get(uuid).getPriority() < sqp.getPriority()) {
                    queue.add(i, p.getUniqueId());
                    return;
                }
            }
            i++;
        }
        queue.add(p.getUniqueId());
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
        p.sendActionBar(Component.empty());
    }

    public void queueUpdate() {
        //this method check if players are still in the queue and remove them if they are not and send them an action bar message of their position in the queue
        for (Map.Entry<String, LinkedList<UUID>> entry : queues.entrySet()) {
            LinkedList<UUID> queue = entry.getValue();
            if (queue == null) continue;
            if (queue.size() == 0) continue;
            ArrayList<UUID> toRemove = new ArrayList<>();
            for (UUID uuid : queue) {
                Player p = OpaleVelocity.INSTANCE.getProxy().getPlayer(uuid).orElse(null);
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
                Player p = OpaleVelocity.INSTANCE.getProxy().getPlayer(uuid).orElse(null);
                if (p == null) continue;
                p.sendActionBar(Component.text("§aServeur : §e" + entry.getKey() + "§a, statut : " +
                        ServerStatusManager.INSTANCE.getServerStatusString(entry.getKey()) +
                        "§a, §e" + (i + 1) + "§8/§e" + queue.size() + "§a dans la file d'attente."));
            }
        }
    }

    public ArrayList<UUID> getIfThereIsPriotairePeople() {
        ArrayList<UUID> prioritaire = new ArrayList<>();
        for (Map.Entry<UUID, ServerQueuePriority> entry : priority.entrySet()) {
            if (entry.getValue() == ServerQueuePriority.PRIORITAIRE) prioritaire.add(entry.getKey());
        }
        return prioritaire;
    }

    public UUID getTheBetterPlacedAndWithPriority(LinkedList<UUID> queue) {
        UUID uuid = null;
        int bestPriority = -1;
        for (UUID uuid1 : queue) {
            if (priority.containsKey(uuid1)) {
                if (priority.get(uuid1).getPriority() > bestPriority) {
                    uuid = uuid1;
                    bestPriority = priority.get(uuid1).getPriority();
                    continue;
                }
            }
        }
        if (uuid == null) {
            try {
                uuid = queue.get(0);
            } catch (Exception e) {
                return null;
            }
        }
        return uuid;
    }

    public void queueTick() {
        //this method process the player to the servers
        queueUpdate();
        for (Map.Entry<String, LinkedList<UUID>> entry : queues.entrySet()) {
            LinkedList<UUID> queue = entry.getValue();
            if (queue == null) continue;
            if (queue.size() == 0) continue;
            ArrayList<UUID> prioritaire = getIfThereIsPriotairePeople();
            if (!prioritaire.isEmpty()) {
                int ticks = 0;
                for (UUID uuid : prioritaire) {
                    if (queue.contains(uuid)) {
                        Player p = OpaleVelocity.INSTANCE.getProxy().getPlayer(uuid).orElse(null);
                        if (p == null) continue;
                        processPlayer(p, entry.getKey(), queue);
                        ticks++;
                    }
                }
                if (skipTicks.containsKey(entry.getKey()))
                    skipTicks.put(entry.getKey(), skipTicks.get(entry.getKey()) + ticks);
                else skipTicks.put(entry.getKey(), ticks);
            }
            if (skipTicks.containsKey(entry.getKey())) {
                int ticks = skipTicks.get(entry.getKey());
                if (ticks > 0) {
                    skipTicks.put(entry.getKey(), ticks - 1);
                    continue;
                }
            }
            if (queue.size() == 0) continue;
            UUID uuid = getTheBetterPlacedAndWithPriority(queue);
            if (uuid == null) continue;
            Player p = OpaleVelocity.INSTANCE.getProxy().getPlayer(uuid).orElse(null);
            if (p == null) continue;
            processPlayer(p, entry.getKey(), queue);
        }
    }

    public void processPlayer(Player p, String server, LinkedList<UUID> queue) {
        if (ServerStatusManager.INSTANCE.getServerStatus(server) == ServerStatus.ON) {

            //TODO: maintenance par serveur

            p.createConnectionRequest(OpaleVelocity.INSTANCE.getProxy().getServer(server).get()).connect().thenAccept((v) -> {
                if (v.isSuccessful()) {
                    queue.remove(p.getUniqueId());
                    p.sendMessage(Component.text("§aVous avez été connecté au serveur §e" + server + "§a avec succès."));
                    p.sendActionBar(Component.text("§aConnexion réussie !"));
                } else {
                    v.getReasonComponent().ifPresent((r) -> p.sendMessage(Component.text("§cUne erreur est survenue lors de la connexion au serveur §e" +
                            server + "§c : " + r)));
                }
            });
        }
    }
}
