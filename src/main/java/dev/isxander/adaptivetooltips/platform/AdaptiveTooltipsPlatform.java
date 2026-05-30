/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.platform;

import java.nio.file.Path;
import java.util.Objects;

public interface AdaptiveTooltipsPlatform {
    Path getConfigDir();

    static AdaptiveTooltipsPlatform get() {
        return Objects.requireNonNull(ImplHolder.IMPL, "No platform implementation found for AdaptiveTooltips");
    }

    class ImplHolder {
        public static AdaptiveTooltipsPlatform IMPL = null;
    }
}
