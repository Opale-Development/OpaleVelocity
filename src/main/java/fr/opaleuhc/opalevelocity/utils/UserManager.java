package fr.opaleuhc.opalevelocity.utils;


import fr.opaleuhc.opalevelocity.OpaleVelocity;
import fr.opaleuhc.opalevelocity.sanctions.ban.BanManager;
import fr.opaleuhc.opalevelocity.sanctions.mute.MuteManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserManager {

    public static UserManager instance;
    public ArrayList<User> users = new ArrayList<>();

    public UserManager() {
        instance = this;

        checkForEndOfSanctions();
    }

    public void checkForEndOfSanctions() {
        OpaleVelocity.instance.getProxy().getScheduler().buildTask(OpaleVelocity.instance, () -> {
            for (User user : users) {
                if (user.isBanned()) {
                    if (user.getBanExpiration() != -1 && user.getBanExpiration() < System.currentTimeMillis()) {
                        BanManager.instance.unban(user);
                    }
                }
                if (user.isMuted()) {
                    if (user.getMuteExpiration() != -1 && user.getMuteExpiration() < System.currentTimeMillis()) {
                        MuteManager.instance.unmute(user);
                    }
                }
            }
        }).repeat(15, TimeUnit.SECONDS).schedule();
    }

    public User createUser(UUID uuid, String pseudo) {
        User user = new User(uuid, pseudo);
        users.add(user);
        CompletableFuture.runAsync(() -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("uuid", uuid.toString());
            params.put("pseudo", pseudo);
            HTTPUtils.makePostRequest(HTTPUtils.baseUrl + "create", params);
        });
        return user;
    }

    public User getAccount(UUID uuid, String pseudo) {
        for (User user : users) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }
        User user = makeGetAndIfNotPresentCreate(uuid, pseudo);
        users.add(user);
        return user;
    }

    public User getAccount(String pseudo) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(pseudo)) {
                return user;
            }
        }
        User user = makeGetFromPseudo(pseudo);
        if (user == null) return null;
        users.add(user);
        return user;
    }

    public User makeGetAndIfNotPresentCreate(UUID uuid, String pseudo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", uuid.toString());
        params.put("pseudo", pseudo);
        String usr = HTTPUtils.makeGetRequest(HTTPUtils.baseUrl + "getandifnotpresentcreate", params);
        if (usr == null) return createUser(uuid, pseudo);
        if (usr.equalsIgnoreCase("created")) return createUser(uuid, pseudo);
        return User.deserialize(usr);
    }

    public User makeGetFromPseudo(String pseudo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("pseudo", pseudo);
        String uuid = HTTPUtils.makeGetRequest(HTTPUtils.baseUrl + "pseudo", params);
        if (uuid == null) return null;
        return getAccount(UUID.fromString(uuid), pseudo);
    }

    public void makePatch(UUID uuid, String key, String value) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", uuid.toString());
        params.put("key", key);
        params.put("value", value);
        HTTPUtils.makePostRequestAsync(HTTPUtils.baseUrl + "update", params);
    }

    public void makePut(User user) {
        HashMap<String, String> params = new HashMap<>();
        params.put("content", User.serialize(user));
        HTTPUtils.makePutRequestAsync(HTTPUtils.baseUrl + "update", params);
    }

}
