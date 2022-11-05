package dev.isxander.adaptivetooltips.config;

import dev.isxander.yacl.api.NameableEnum;
import net.minecraft.text.Text;

public enum ScrollDirection implements NameableEnum {
    REVERSE,
    NATURAL;

    @Override
    public Text getDisplayName() {
        return Text.translatable("adaptivetooltips.scroll_direction." + name().toLowerCase());
    }
}
