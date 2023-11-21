package me.diffusehyperion.testingcustomhandshake.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundUpgradedStatusRequestPacket implements Packet<ServerUpgradedStatusPacketListener> {
    public ServerboundUpgradedStatusRequestPacket() {
    }

    public ServerboundUpgradedStatusRequestPacket(FriendlyByteBuf friendlyByteBuf) {
    }
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public void handle(ServerUpgradedStatusPacketListener packetListener) {
        packetListener.handleUpgradedStatusResponse(this);
    }
}
