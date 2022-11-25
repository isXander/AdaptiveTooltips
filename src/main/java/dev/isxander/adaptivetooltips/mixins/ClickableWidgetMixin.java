package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.YACLTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClickableWidget.class)
public class ClickableWidgetMixin {
    @ModifyReturnValue(method = "getTooltipPositioner", at = @At("RETURN"))
    private TooltipPositioner changePositioner(TooltipPositioner tooltipPositioner) {
        if (AdaptiveTooltipConfig.INSTANCE.getConfig().useYACLTooltipPositioner)
            return new YACLTooltipPositioner((ClickableWidget) (Object) this);
        return tooltipPositioner;
    }
}
