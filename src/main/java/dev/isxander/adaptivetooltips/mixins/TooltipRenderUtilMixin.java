package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
    @WrapOperation(
            method = "extractTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")
    )
    private static void changeBackgroundColor(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier id, int i, int j, int k, int l, Operation<Void> original) {
        instance.blitSprite(renderPipeline, id, i, j, k, l, AdaptiveTooltipConfig.HANDLER.instance().tooltipTransparency);
    }
}
