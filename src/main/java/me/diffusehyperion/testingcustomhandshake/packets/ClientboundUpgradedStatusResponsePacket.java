package me.diffusehyperion.testingcustomhandshake.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundUpgradedStatusResponsePacket implements Packet<ClientUpgradedStatusPacketListener> {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void handle(ClientUpgradedStatusPacketListener packetListener) {
        packetListener.handleUpgradedStatusResponse(this);
    }
}
