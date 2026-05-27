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
    private ClientTooltipPositioner changePositioner(ClientTooltipPositioner tooltipPositioner, @Local(argsOnly = true) ScreenRectangle focus) {
        return AdaptiveTooltipConfig.HANDLER.instance().useYACLTooltipPositioner ? new YACLTooltipPositioner(focus) : tooltipPositioner;
    }
}
