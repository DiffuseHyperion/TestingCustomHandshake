package me.diffusehyperion.testingcustomhandshake.client;

import me.diffusehyperion.testingcustomhandshake.util.Scheduler;
import net.fabricmc.api.ClientModInitializer;

public class TestingCustomHandshakeClient implements ClientModInitializer {
    public static Scheduler clientScheduler = new Scheduler();

    @Override
    public void onInitializeClient() {
    }
}
