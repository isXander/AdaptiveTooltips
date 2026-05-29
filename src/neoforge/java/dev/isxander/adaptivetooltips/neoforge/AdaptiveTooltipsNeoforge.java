package dev.isxander.adaptivetooltips.neoforge;

import dev.isxander.adaptivetooltips.AdaptiveTooltips;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.platform.AdaptiveTooltipsPlatform;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("adaptive_tooltips")
public class AdaptiveTooltipsNeoforge {
    public AdaptiveTooltipsNeoforge() {
        AdaptiveTooltipsPlatform.ImplHolder.IMPL = new AdaptiveTooltipsPlatformNeoforge();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (_, parent) -> AdaptiveTooltipConfig.HANDLER.generateGui().generateScreen(parent)
        );

        AdaptiveTooltips.onInitializeClient();
    }
}
