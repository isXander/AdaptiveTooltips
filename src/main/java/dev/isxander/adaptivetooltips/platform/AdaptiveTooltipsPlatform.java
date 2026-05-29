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
