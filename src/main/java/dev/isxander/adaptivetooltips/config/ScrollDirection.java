package dev.isxander.adaptivetooltips.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;

public enum ScrollDirection implements NameableEnum {
    REVERSE,
    NATURAL;

    @Override
    public Component getDisplayName() {
        return Component.translatable("adaptivetooltips.scroll_direction." + name().toLowerCase());
    }
}
