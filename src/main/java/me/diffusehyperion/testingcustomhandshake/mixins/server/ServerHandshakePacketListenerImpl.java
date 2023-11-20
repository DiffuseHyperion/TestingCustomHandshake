package me.diffusehyperion.testingcustomhandshake.mixins.server;

import com.llamalad7.mixinextras.sugar.Local;
import me.diffusehyperion.testingcustomhandshake.packets.ServerUpgradedStatusPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.status.ServerStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(net.minecraft.server.network.ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImpl {
    @Final
    @Shadow
    private Connection connection;

    @ModifyArg(method = "handleIntention",
    at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketListener;)V", ordinal = 1))
    private PacketListener handleIntention(PacketListener packetListener, @Local ServerStatus serverStatus) {
        return new ServerUpgradedStatusPacketListenerImpl(serverStatus, connection);
    }
}
