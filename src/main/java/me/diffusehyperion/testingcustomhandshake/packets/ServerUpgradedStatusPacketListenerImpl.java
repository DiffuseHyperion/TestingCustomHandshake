package me.diffusehyperion.testingcustomhandshake.packets;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.*;

public class ServerUpgradedStatusPacketListenerImpl implements ServerUpgradedStatusPacketListener {

    @Override
    public void handleUpgradedStatusResponse(ServerboundUpgradedStatusRequestPacket serverboundUpgradedStatusRequestPacket) {
        this.connection.send(new ClientboundUpgradedStatusResponsePacket());
    }

    private static final Component DISCONNECT_REASON = Component.translatable("multiplayer.status.request_handled");
    private final ServerStatus status;
    private final Connection connection;
    private boolean hasRequestedStatus;

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
        if (this.hasRequestedStatus) {
            this.connection.disconnect(DISCONNECT_REASON);
        } else {
            this.hasRequestedStatus = true;
            this.connection.send(new ClientboundStatusResponsePacket(this.status));
        }
    }

    public void handlePingRequest(ServerboundPingRequestPacket serverboundPingRequestPacket) {
        this.connection.send(new ClientboundPongResponsePacket(serverboundPingRequestPacket.getTime()));
        this.connection.disconnect(DISCONNECT_REASON);
    }
}