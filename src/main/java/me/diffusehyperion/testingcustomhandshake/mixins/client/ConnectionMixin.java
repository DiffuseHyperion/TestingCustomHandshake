package me.diffusehyperion.testingcustomhandshake.mixins.client;

import io.netty.channel.ChannelHandlerContext;
import me.diffusehyperion.testingcustomhandshake.packets.ClientUpgradedStatusPacketListener;
import me.diffusehyperion.testingcustomhandshake.client.interfaces.ConnectionMixinInterface;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ClientIntent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
    at = @At(value = "HEAD"))
    private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {

    }
}
