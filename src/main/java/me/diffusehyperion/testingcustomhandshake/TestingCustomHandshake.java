package me.diffusehyperion.testingcustomhandshake;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingCustomHandshake implements ModInitializer {
    public static final Logger MODLOGGER = LoggerFactory.getLogger("TestingCustomHandshake");

    @Override
    public void onInitialize() {
        MODLOGGER.info("let the shitshow begin");
    }
}
