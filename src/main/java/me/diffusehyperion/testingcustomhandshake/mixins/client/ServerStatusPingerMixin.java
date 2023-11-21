package me.diffusehyperion.testingcustomhandshake.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import me.diffusehyperion.testingcustomhandshake.client.interfaces.ConnectionMixinInterface;
import me.diffusehyperion.testingcustomhandshake.packets.ClientUpgradedStatusPacketListener;
import me.diffusehyperion.testingcustomhandshake.packets.ClientboundUpgradedStatusResponsePacket;
import me.diffusehyperion.testingcustomhandshake.packets.ServerboundUpgradedStatusRequestPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.*;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.diffusehyperion.testingcustomhandshake.TestingCustomHandshake.MODLOGGER;

@Debug(export = true)
@Mixin(ServerStatusPinger.class)
public class ServerStatusPingerMixin {

    @Shadow
    void onPingFailed(Component component, ServerData serverData) {}

    @Shadow
    void pingLegacyServer(InetSocketAddress inetSocketAddress, final ServerAddress serverAddress, final ServerData serverData) {}

    // just a bit scuffed
    // mixinextras is a lifesaver
    @Inject(method = "pingServer",
    at = @At(value = "HEAD"))
    private void pingServer(ServerData serverData, Runnable runnable, CallbackInfo ci,
                            @Share("serverData") LocalRef<ServerData> serverDataLocalRef,
                            @Share("runnable") LocalRef<Runnable> runnableLocalRef) {
        serverDataLocalRef.set(serverData);
        runnableLocalRef.set(runnable);
    }

    // mixinextras coming in clutch again
    @Redirect(method = "pingServer",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;initiateServerboundStatusConnection(Ljava/lang/String;ILnet/minecraft/network/protocol/status/ClientStatusPacketListener;)V"))
    private void initiateServerboundUpgradedStatusConnection(
            Connection connection, String host, int port, ClientStatusPacketListener clientStatusPacketListener,
            @Share("serverData") LocalRef<ServerData> serverDataLocalRef,
            @Share("runnable") LocalRef<Runnable> runnableLocalRef,
            @Local InetSocketAddress inetSocketAddress,
            @Local ServerAddress serverAddress) {
        ServerData serverData = serverDataLocalRef.get();
        Runnable runnable = runnableLocalRef.get();
        ClientUpgradedStatusPacketListener listener = new ClientUpgradedStatusPacketListener() {
            @Override
            public void handleUpgradedStatusResponse(ClientboundUpgradedStatusResponsePacket var1) {
                MODLOGGER.info("Received upgraded response packet from " + serverAddress.getHost() + ":" + serverAddress.getPort());
            }

            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            public void handleStatusResponse(ClientboundStatusResponsePacket clientboundStatusResponsePacket) {
                MODLOGGER.info("Received vanilla response packet from " + serverAddress.getHost() + ":" + serverAddress.getPort());
                if (this.receivedPing) {
                    connection.disconnect(Component.translatable("multiplayer.status.unrequested"));
                } else {
                    this.receivedPing = true;
                    ServerStatus serverStatus = clientboundStatusResponsePacket.status();
                    serverData.motd = serverStatus.description();
                    serverStatus.version().ifPresentOrElse((version) -> {
                        serverData.version = Component.literal(version.name());
                        serverData.protocol = version.protocol();
                    }, () -> {
                        serverData.version = Component.translatable("multiplayer.status.old");
                        serverData.protocol = 0;
                    });
                    serverStatus.players().ifPresentOrElse((players) -> {
                        serverData.status = ServerStatusPinger.formatPlayerCount(players.online(), players.max());
                        serverData.players = players;
                        if (!players.sample().isEmpty()) {
                            List<Component> list = new ArrayList<>(players.sample().size());

                            for (GameProfile gameProfile : players.sample()) {
                                list.add(Component.literal(gameProfile.getName()));
                            }

                            if (players.sample().size() < players.online()) {
                                list.add(Component.translatable("multiplayer.status.and_more", players.online() - players.sample().size()));
                            }

                            serverData.playerList = list;
                        } else {
                            serverData.playerList = List.of();
                        }

                    }, () -> serverData.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY));
                    serverStatus.favicon().ifPresent((favicon) -> {
                        if (!Arrays.equals(favicon.iconBytes(), serverData.getIconBytes())) {
                            serverData.setIconBytes(ServerData.validateIcon(favicon.iconBytes()));
                            runnable.run();
                        }

                    });
                    this.pingStart = Util.getMillis();
                    connection.send(new ServerboundPingRequestPacket(this.pingStart));
                    this.success = true;
                }
            }

            @Override
            public void handlePongResponse(ClientboundPongResponsePacket clientboundPongResponsePacket) {
                long l = this.pingStart;
                long m = Util.getMillis();
                serverData.ping = m - l;
                connection.disconnect(Component.translatable("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnect(Component component) {
                if (!this.success) {
                    ServerStatusPingerMixin.this.onPingFailed(component, serverData);
                    ServerStatusPingerMixin.this.pingLegacyServer(inetSocketAddress, serverAddress, serverData);
                }
            }

            @Override
            public boolean isAcceptingMessages() {
                return connection.isConnected();
            }
        };
        ((ConnectionMixinInterface) connection).testingCustomHandshake$initiateServerboundUpgradedStatusConnection(host, port, listener);
        MODLOGGER.info("Sending upgraded request packet to " + serverAddress.getHost() + ":" + serverAddress.getPort());
        connection.send(new ServerboundUpgradedStatusRequestPacket());
    }
}