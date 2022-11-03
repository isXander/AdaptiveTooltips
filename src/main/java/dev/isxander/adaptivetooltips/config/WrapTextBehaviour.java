package dev.isxander.adaptivetooltips.config;

import dev.isxander.yacl.api.NameableEnum;
import net.minecraft.text.Text;

public enum WrapTextBehaviour implements NameableEnum {
    OFF,
    SCREEN_WIDTH,
    REMAINING_WIDTH;

    @Override
    public Text getDisplayName() {
        return Text.translatable("adaptivetooltips.wrap_text_behaviour." + name().toLowerCase());
    }
}
