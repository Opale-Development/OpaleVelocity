package fr.opaleuhc.opalevelocity.cpm;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Optional;

public class CustomPluginMessageListener {

    public static CustomPluginMessageListener instance;
    private final ProxyServer proxy;
    private final Logger logger;
    public ChannelIdentifier channelIdentifier;

    public CustomPluginMessageListener(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        instance = this;

        channelIdentifier = MinecraftChannelIdentifier.from("opaleuhc:update");

        proxy.getChannelRegistrar().register(channelIdentifier);

        logger.info("Loaded CustomPluginMessageListener.");
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getIdentifier().getId().equals(channelIdentifier.getId()))
            return;
        if (!(e.getTarget() instanceof Player)) return;
        e.setResult(PluginMessageEvent.ForwardResult.handled());
        handleMessage((Player) e.getTarget(), e.getData(), e.getIdentifier().getId());
    }

    public void handleMessage(Player player, byte[] data, String channel) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        try {
            String subchannel = in.readUTF();
            String msg = in.readUTF();

            System.out.println("Received message from " + player.getUsername() + " on channel " + channel + " with subchannel " + subchannel + " and message " + msg);

            if (channel.equals("account")) {

            }
        } catch (Exception e) {
            logger.error("Error while handling message", e);
        }
    }

    //sendPluginMessage(player, "pay", null, rawData);

    public void sendPluginMessage(Player player, String channel, String... data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        for (String s : data) {
            out.writeUTF(s);
        }
        Optional<ServerConnection> server = player.getCurrentServer();
        server.ifPresent(serverConnection -> serverConnection.sendPluginMessage(channelIdentifier, out.toByteArray()));
    }
}
