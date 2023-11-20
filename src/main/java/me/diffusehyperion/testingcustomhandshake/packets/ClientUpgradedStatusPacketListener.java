package me.diffusehyperion.testingcustomhandshake.packets;

import net.minecraft.network.protocol.status.ClientStatusPacketListener;

public interface ClientUpgradedStatusPacketListener extends ClientStatusPacketListener {
    void handleUpgradedStatusResponse(ClientboundUpgradedStatusResponsePacket var1);
}
