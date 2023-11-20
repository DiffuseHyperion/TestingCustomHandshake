package me.diffusehyperion.testingcustomhandshake.client.interfaces;

import me.diffusehyperion.testingcustomhandshake.packets.ClientUpgradedStatusPacketListener;

public interface ConnectionMixinInterface {

    void testingCustomHandshake$initiateServerboundUpgradedStatusConnection(String string, int i, ClientUpgradedStatusPacketListener clientUpgradedStatusPacketListener);

    boolean testingCustomHandshake$isUpgraded();

}
