package dev.isxander.adaptivetooltips.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AdaptiveTooltipConfig::makeScreen;
    }
}
