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
