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

public enum ScrollDirection implements NameableEnum {
	REVERSE,
	NATURAL;

	@Override
	public Component getDisplayName() {
		return Component.translatable("adaptivetooltips.scroll_direction." + name().toLowerCase());
	}
}
