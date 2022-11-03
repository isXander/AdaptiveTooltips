package dev.isxander.adaptivetooltips;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class AdaptiveTooltips {
    public static final KeyBinding SCROLL_BIND = new KeyBinding("adaptivetooltips.bind.scroll", InputUtil.GLFW_KEY_LEFT_ALT, "adaptivetooltips.title");
    public static final KeyBinding HORIZONTAL_SCROLL_BIND = new KeyBinding("adaptivetooltips.bind.horizontal_scroll", InputUtil.GLFW_KEY_LEFT_CONTROL, "adaptivetooltips.title");

    public static void onInitializeClient() {
        AdaptiveTooltipConfig.load();

        KeyBindingHelper.registerKeyBinding(SCROLL_BIND);
        KeyBindingHelper.registerKeyBinding(HORIZONTAL_SCROLL_BIND);
    }
}
