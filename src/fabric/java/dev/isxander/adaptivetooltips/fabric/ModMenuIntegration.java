/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AdaptiveTooltipConfig.createGui().generateScreen(parent);
	}
}
