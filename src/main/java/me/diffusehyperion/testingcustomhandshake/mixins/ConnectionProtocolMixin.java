package me.diffusehyperion.testingcustomhandshake.mixins;

import me.diffusehyperion.testingcustomhandshake.packets.ClientboundUpgradedStatusResponsePacket;
import me.diffusehyperion.testingcustomhandshake.packets.ServerboundUpgradedStatusRequestPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Debug(export = true) // Enables exporting for the targets of this mixin
@Mixin(ConnectionProtocol.class)
public class ConnectionProtocolMixin {

    //	 targeted by slice -> | STATUS("status",
    //	                      | protocol()
    //	         ordinal 0 -> | .addFlow(PacketFlow.SERVERBOUND, (new PacketSet()).addPacket(ServerboundStatusRequestPacket.class, ServerboundStatusRequestPacket::new).addPacket(ServerboundPingRequestPacket.class, ServerboundPingRequestPacket::new))
    //	         ordinal 1 -> | .addFlow(PacketFlow.CLIENTBOUND, (new PacketSet()).addPacket(ClientboundStatusResponsePacket.class, ClientboundStatusResponsePacket::new).addPacket(ClientboundPongResponsePacket.class, ClientboundPongResponsePacket::new))
    //	                      | ),

    // something went wrong with generics so
    @ModifyArg(method = "<clinit>",
    at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/ConnectionProtocol$ProtocolBuilder;addFlow(Lnet/minecraft/network/protocol/PacketFlow;Lnet/minecraft/network/ConnectionProtocol$PacketSet;)Lnet/minecraft/network/ConnectionProtocol$ProtocolBuilder;",
            ordinal = 0 ), // clientbound
    index = 1,
    slice = @Slice(
            from = @At(value = "CONSTANT",
                    args = "stringValue=status"
            )
    ))
    private static ConnectionProtocol.PacketSet registerServerbound(ConnectionProtocol.PacketSet packetSet) {
        packetSet.addPacket(ClientboundUpgradedStatusResponsePacket.class, o -> new ClientboundUpgradedStatusResponsePacket((FriendlyByteBuf) o));
        return packetSet;
    }

    @ModifyArg(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/ConnectionProtocol$ProtocolBuilder;addFlow(Lnet/minecraft/network/protocol/PacketFlow;Lnet/minecraft/network/ConnectionProtocol$PacketSet;)Lnet/minecraft/network/ConnectionProtocol$ProtocolBuilder;",
                    ordinal = 1 ), // serveround
            index = 1,
            slice = @Slice(
                    from = @At(value = "CONSTANT",
                            args = "stringValue=status"
                    )
            ))
    private static ConnectionProtocol.PacketSet initServerbound(ConnectionProtocol.PacketSet packetSet) {
        packetSet.addPacket(ServerboundUpgradedStatusRequestPacket.class, o -> new ServerboundUpgradedStatusRequestPacket((FriendlyByteBuf) o));
        return packetSet;
    }
}
