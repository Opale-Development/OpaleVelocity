package fr.opaleuhc.opalevelocity.dependencies;

import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;

import java.util.UUID;

public class LuckPerms {
    public static LuckPerms INSTANCE;
    private final ProxyServer server;
    private final Logger logger;
    private final net.luckperms.api.LuckPerms luckPermsAPI;

    public LuckPerms(ProxyServer server, Logger logger, net.luckperms.api.LuckPerms luckPermsAPI) {
        INSTANCE = this;
        this.server = server;
        this.logger = logger;

        this.luckPermsAPI = luckPermsAPI;

        logger.info("Loaded LuckPerms API !");
    }

    public String getPrefix(UUID uuid) {
        User user = luckPermsAPI.getUserManager().getUser(uuid);
        String prefix = user != null ? user.getCachedData().getMetaData().getPrefix() : "";
        return prefix != null ? prefix.replace("&", "§") : "";
    }

    public String getSuffix(UUID uuid) {
        User user = luckPermsAPI.getUserManager().getUser(uuid);
        String suffix = user != null ? user.getCachedData().getMetaData().getSuffix() : "";
        return suffix != null ? suffix.replace("&", "§") : "";
    }

}
