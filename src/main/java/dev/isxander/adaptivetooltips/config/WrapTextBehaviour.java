/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
