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

import dev.isxander.adaptivetooltips.platform.AdaptiveTooltipsPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class AdaptiveTooltipsPlatformFabric implements AdaptiveTooltipsPlatform {
    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
