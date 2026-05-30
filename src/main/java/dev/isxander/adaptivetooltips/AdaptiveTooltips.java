package dev.isxander.adaptivetooltips;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.resources.Identifier;

public class AdaptiveTooltips {
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("adaptive_tooltips", path);
    }

    public static void onInitializeClient() {
        AdaptiveTooltipConfig.HANDLER.load();
    }
}
