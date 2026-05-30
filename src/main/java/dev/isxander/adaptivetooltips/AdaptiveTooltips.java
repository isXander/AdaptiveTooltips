/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
