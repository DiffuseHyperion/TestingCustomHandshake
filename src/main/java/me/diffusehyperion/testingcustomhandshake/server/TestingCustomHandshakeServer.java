package me.diffusehyperion.testingcustomhandshake.server;

import me.diffusehyperion.testingcustomhandshake.util.Scheduler;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TestingCustomHandshakeServer implements DedicatedServerModInitializer {
    public static Scheduler serverScheduler = new Scheduler();
    @Override
    public void onInitializeServer() {
    }
}
