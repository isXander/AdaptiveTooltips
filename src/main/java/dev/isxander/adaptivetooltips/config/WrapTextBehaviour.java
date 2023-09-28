package dev.isxander.adaptivetooltips.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;

public enum WrapTextBehaviour implements NameableEnum {
    OFF,
    SCREEN_WIDTH,
    REMAINING_WIDTH,
    HALF_SCREEN_WIDTH,
    SMART;

    @Override
    public Component getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getTooltip() {
        return Component.translatable(getTranslationKey() + ".desc");
    }

    private String getTranslationKey() {
        return "adaptivetooltips.wrap_text_behaviour." + name().toLowerCase();
    }
}
