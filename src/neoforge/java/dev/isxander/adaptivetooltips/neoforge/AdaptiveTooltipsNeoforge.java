/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.neoforge;

import dev.isxander.adaptivetooltips.AdaptiveTooltips;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.platform.AdaptiveTooltipsPlatform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "adaptive_tooltips", dist = Dist.CLIENT)
public class AdaptiveTooltipsNeoforge {
	public AdaptiveTooltipsNeoforge() {
		AdaptiveTooltipsPlatform.ImplHolder.IMPL = new AdaptiveTooltipsPlatformNeoforge();

		ModLoadingContext.get().registerExtensionPoint(
				IConfigScreenFactory.class,
				() -> (_, parent) -> AdaptiveTooltipConfig.createGui().generateScreen(parent)
		);

		AdaptiveTooltips.onInitializeClient();
	}
}
