package dev.isxander.adaptivetooltips;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;

public class AdaptiveTooltips {
    public static void onInitializeClient() {
        AdaptiveTooltipConfig.INSTANCE.load();
    }
}
