package fr.opaleuhc.opalevelocity.serverstatus;

public enum ServerStatus {

    ON("§aEn ligne"),
    OFF("§cHors ligne");

    private final String status;

    ServerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
