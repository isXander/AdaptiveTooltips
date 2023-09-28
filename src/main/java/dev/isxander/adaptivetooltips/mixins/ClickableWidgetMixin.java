package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.YACLTooltipPositioner;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractWidget.class)
public class ClickableWidgetMixin {
    @ModifyReturnValue(method = "createTooltipPositioner", at = @At("RETURN"))
    private ClientTooltipPositioner changePositioner(ClientTooltipPositioner tooltipPositioner) {
        if (AdaptiveTooltipConfig.HANDLER.instance().useYACLTooltipPositioner)
            return new YACLTooltipPositioner((AbstractWidget) (Object) this);
        return tooltipPositioner;
    }
}
