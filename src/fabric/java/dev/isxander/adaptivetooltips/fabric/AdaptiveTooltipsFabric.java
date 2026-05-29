package dev.isxander.adaptivetooltips.fabric;

import dev.isxander.adaptivetooltips.AdaptiveTooltips;
import dev.isxander.adaptivetooltips.platform.AdaptiveTooltipsPlatform;
import net.fabricmc.api.ClientModInitializer;

public class AdaptiveTooltipsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AdaptiveTooltipsPlatform.ImplHolder.IMPL = new AdaptiveTooltipsPlatformFabric();

        AdaptiveTooltips.onInitializeClient();
    }
}
