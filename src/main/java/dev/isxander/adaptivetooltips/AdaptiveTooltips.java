package dev.isxander.adaptivetooltips;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.config.gui.KeyCode;
import dev.isxander.yacl3.config.v2.api.autogen.OptionFactory;

public class AdaptiveTooltips {
    public static void onInitializeClient() {
        AdaptiveTooltipConfig.HANDLER.load();
        OptionFactory.register(KeyCode.class, new KeyCode.KeyCodeImpl());
    }
}
