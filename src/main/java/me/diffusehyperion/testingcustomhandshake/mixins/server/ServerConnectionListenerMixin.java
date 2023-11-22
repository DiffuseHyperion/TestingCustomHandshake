package me.diffusehyperion.testingcustomhandshake.mixins.server;

import me.diffusehyperion.testingcustomhandshake.server.TestingCustomHandshakeServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConnectionListener.class)
public class ServerConnectionListenerMixin {
    @Inject(method = "tick",
    at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        TestingCustomHandshakeServer.serverScheduler.tick();
    }
}
