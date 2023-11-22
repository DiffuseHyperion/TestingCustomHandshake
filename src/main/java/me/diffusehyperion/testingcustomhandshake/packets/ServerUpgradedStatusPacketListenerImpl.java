package me.diffusehyperion.testingcustomhandshake.packets;

import me.diffusehyperion.testingcustomhandshake.server.TestingCustomHandshakeServer;
import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.*;

import static me.diffusehyperion.testingcustomhandshake.TestingCustomHandshake.MODLOGGER;

public class ServerUpgradedStatusPacketListenerImpl implements ServerUpgradedStatusPacketListener {

    @Override
    public void handleUpgradedStatusResponse(ServerboundUpgradedStatusRequestPacket serverboundUpgradedStatusRequestPacket) {
        MODLOGGER.info("Received upgraded request packet, sending upgraded response packet");
        this.connection.send(new ClientboundUpgradedStatusResponsePacket());
        TestingCustomHandshakeServer.serverScheduler.cancelTask(disconnectRunnable);
        disconnectRunnable.run();
    }

    private static final Component DISCONNECT_REASON = Component.translatable("multiplayer.status.request_handled");
    private final ServerStatus status;
    private final Connection connection;
    private boolean hasRequestedStatus;
    private long startTime;

    private final Runnable disconnectRunnable = new Runnable() {
        @Override
        public void run() {
            connection.disconnect(DISCONNECT_REASON);
        }
    };

    public ServerUpgradedStatusPacketListenerImpl(ServerStatus serverStatus, Connection connection) {
        this.status = serverStatus;
        this.connection = connection;
    }

    public void onDisconnect(Component component) {
    }

    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public void handleStatusRequest(ServerboundStatusRequestPacket serverboundStatusRequestPacket) {
        MODLOGGER.info("Received vanilla request packet, sending vanilla response packet");
        this.startTime = Util.getMillis();

        if (this.hasRequestedStatus) {
            this.connection.disconnect(DISCONNECT_REASON);
        } else {
            this.hasRequestedStatus = true;
            this.connection.send(new ClientboundStatusResponsePacket(this.status));
        }
    }

    public void handlePingRequest(ServerboundPingRequestPacket serverboundPingRequestPacket) {
        this.connection.send(new ClientboundPongResponsePacket(serverboundPingRequestPacket.getTime()));

        long timeTaken = Util.getMillis() - this.startTime;
        TestingCustomHandshakeServer.serverScheduler.addTask((int) ((timeTaken / 50) + 20), disconnectRunnable);
    }
}
