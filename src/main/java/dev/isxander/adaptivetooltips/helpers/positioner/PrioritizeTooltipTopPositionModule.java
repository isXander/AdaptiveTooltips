/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.helpers.positioner;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Optional;

public class PrioritizeTooltipTopPositionModule implements TooltipPositionModule {
    @Override
    public Optional<Vector2ic> repositionTooltip(int x, int y, int width, int height, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (!AdaptiveTooltipConfig.HANDLER.instance().prioritizeTooltipTop || height <= screenHeight)
            return Optional.empty();

        return Optional.of(new Vector2i(x, 4));
    }
}
