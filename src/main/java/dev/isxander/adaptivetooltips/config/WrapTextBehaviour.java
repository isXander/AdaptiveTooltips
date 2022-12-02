package dev.isxander.adaptivetooltips.config;

import dev.isxander.yacl.api.NameableEnum;
import net.minecraft.text.Text;

public enum WrapTextBehaviour implements NameableEnum {
    OFF,
    SCREEN_WIDTH,
    REMAINING_WIDTH,
    HALF_SCREEN_WIDTH,
    SMART;

    @Override
    public Text getDisplayName() {
        return Text.translatable(getTranslationKey());
    }

    public Text getTooltip() {
        return Text.translatable(getTranslationKey() + ".desc");
    }

    private String getTranslationKey() {
        return "adaptivetooltips.wrap_text_behaviour." + name().toLowerCase();
    }
}
