package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
    @ModifyExpressionValue(
            method = "renderTooltipBackground",
            at = {
                    @At(value = "CONSTANT", args = "intValue=-267386864"),
                    @At(value = "CONSTANT", args = "intValue=1347420415"),
                    @At(value = "CONSTANT", args = "intValue=1344798847"),
            }
    )
    private static int changeBackgroundColor(int original) {
        Color prevColor = new Color(original, true);
        return new Color(
                prevColor.getRed(),
                prevColor.getGreen(),
                prevColor.getBlue(),
                (int) Mth.clamp(
                        prevColor.getAlpha() * AdaptiveTooltipConfig.HANDLER.instance().tooltipTransparency,
                        0, 255
                )
        ).getRGB();
    }
}
