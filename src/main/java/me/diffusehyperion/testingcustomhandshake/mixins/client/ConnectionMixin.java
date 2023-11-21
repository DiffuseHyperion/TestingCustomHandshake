package me.diffusehyperion.testingcustomhandshake.mixins.client;

import me.diffusehyperion.testingcustomhandshake.client.interfaces.ConnectionMixinInterface;
import me.diffusehyperion.testingcustomhandshake.packets.ClientUpgradedStatusPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.handshake.ClientIntent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Connection.class)
public abstract class ConnectionMixin implements ConnectionMixinInterface {
    @Unique
    private boolean upgraded;

    @Unique
    @Override
    public void testingCustomHandshake$initiateServerboundUpgradedStatusConnection(String string, int i, ClientUpgradedStatusPacketListener clientUpgradedStatusPacketListener) {
        this.upgraded = true;
        this.initiateServerboundConnection(string, i, clientUpgradedStatusPacketListener, ClientIntent.STATUS);
    }

    @Unique
    @Override
    public boolean testingCustomHandshake$isUpgraded() {
        return upgraded;
    }

    @Shadow
    private void initiateServerboundConnection(String string, int i, PacketListener packetListener, ClientIntent clientIntent) {}
}
