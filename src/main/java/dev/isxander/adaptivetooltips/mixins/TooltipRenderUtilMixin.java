package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
    @WrapOperation(
            method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V")
    )
    private static void changeBackgroundColor(GuiGraphics instance, Function<ResourceLocation, RenderType> function, ResourceLocation id, int i, int j, int k, int l, Operation<Void> original) {
        instance.blitSprite(function, id, i, j, k, l, (int) (AdaptiveTooltipConfig.HANDLER.instance().tooltipTransparency * 255f) << 24 | 0xFFFFFF);
    }
}
