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
import fr.opaleuhc.opalevelocity.cmd.DiscordCmd;
import fr.opaleuhc.opalevelocity.cmd.MumbleCmd;
import fr.opaleuhc.opalevelocity.cmd.TwitterCmd;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Optional;

public class CPMListener {

    public static CPMListener INSTANCE;
    private final ProxyServer proxy;
    private final Logger logger;
    public ChannelIdentifier channelIdentifier;
    public ChannelIdentifier sanctionsChannelIdentifier;

    public CPMListener(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        INSTANCE = this;

        channelIdentifier = MinecraftChannelIdentifier.from("opaleuhc:update");
        sanctionsChannelIdentifier = MinecraftChannelIdentifier.from("opaleuhc:sanctions");

        proxy.getChannelRegistrar().register(channelIdentifier);
        proxy.getChannelRegistrar().register(sanctionsChannelIdentifier);

        logger.info("Loaded CustomPluginMessageListener.");
    }

    public void checkForSanctionChannel(PluginMessageEvent e) {
        if (!e.getIdentifier().getId().equals(sanctionsChannelIdentifier.getId()))
            return;
        if (!(e.getTarget() instanceof Player p)) return;
        e.setResult(PluginMessageEvent.ForwardResult.handled());
        handleSanctionMessage(p, e.getData(), e.getIdentifier().getId());
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {
        checkForSanctionChannel(e);
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
            if (subchannel.equals("want")) {
                switch (msg) {
                    case "discord" -> DiscordCmd.sendDiscord(player);
                    case "mumble" -> MumbleCmd.sendMumble(player);
                    case "twitter" -> TwitterCmd.sendTwitter(player);
                }
                return;
            }
        } catch (Exception e) {
            logger.error("Error while handling message", e);
        }
    }

    public void handleSanctionMessage(Player p, byte[] data, String channel) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        try {
            String subchannel = in.readUTF();
            String msg = in.readUTF();

            System.out.println("Received message from " + p.getUsername() + " on channel " + channel + " with subchannel " + subchannel + " and message " + msg);

            if (subchannel.equals("mute")) {
                System.out.println("Received mute cpm message from " + p.getUsername());
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
