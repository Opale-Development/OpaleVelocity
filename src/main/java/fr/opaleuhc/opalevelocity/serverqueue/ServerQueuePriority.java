package fr.opaleuhc.opalevelocity.serverqueue;

public enum ServerQueuePriority {

    BASSE(0, "§7Basse"),
    NORMALE(1, "§aNormale"),
    HAUTE(2, "§a§lHaute"),
    PRIORITAIRE(4, "§c§lPrioritaire");

    private final int priority;
    private final String name;

    ServerQueuePriority(int priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

}
