/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseMixin {
    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(
            method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"
            )
    )
    private boolean trackMouseWheel(
            Screen screen,
            double mouseX, double mouseY,
            double horizontalAmount, double verticalAmount,
            Operation<Boolean> original,
            long handle,
            double xoffset, double yoffset
    ) {
        if (InputConstants.isKeyDown(minecraft.getWindow(), AdaptiveTooltipConfig.HANDLER.instance().scrollKeyCode)) {
            if (InputConstants.isKeyDown(minecraft.getWindow(), AdaptiveTooltipConfig.HANDLER.instance().horizontalScrollKeyCode)) {
                ScrollTracker.addHorizontalScroll((int) Math.signum(yoffset));
            } else {
                ScrollTracker.addVerticalScroll((int) Math.signum(yoffset));
                ScrollTracker.addHorizontalScroll((int) Math.signum(xoffset));
            }
            return false;
        }

        return original.call(screen, mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
