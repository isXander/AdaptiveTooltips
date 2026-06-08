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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.YACLTooltipPositioner;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WidgetTooltipHolder.class)
public class WidgetTooltipHolderMixin {
	@ModifyReturnValue(method = "createTooltipPositioner", at = @At("RETURN"))
	private ClientTooltipPositioner changePositioner(
			ClientTooltipPositioner tooltipPositioner,
			@Local(argsOnly = true, name = "screenRectangle") ScreenRectangle screenRectangle
	) {
		return AdaptiveTooltipConfig.HANDLER.instance().useYACLTooltipPositioner
				? new YACLTooltipPositioner(screenRectangle)
				: tooltipPositioner;
	}
}
