package me.diffusehyperion.testingcustomhandshake.packets;

import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public interface ServerUpgradedStatusPacketListener extends ServerStatusPacketListener {
    void handleUpgradedStatusResponse(ServerboundUpgradedStatusRequestPacket var1);
}
