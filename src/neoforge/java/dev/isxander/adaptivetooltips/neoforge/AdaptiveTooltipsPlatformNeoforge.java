package dev.isxander.adaptivetooltips.neoforge;

import dev.isxander.adaptivetooltips.platform.AdaptiveTooltipsPlatform;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class AdaptiveTooltipsPlatformNeoforge implements AdaptiveTooltipsPlatform {
    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
