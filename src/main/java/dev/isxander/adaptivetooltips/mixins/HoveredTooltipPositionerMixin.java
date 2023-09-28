package dev.isxander.adaptivetooltips.mixins;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DefaultTooltipPositioner.class)
public class HoveredTooltipPositionerMixin {
    @ModifyArg(method = "positionTooltip(IILorg/joml/Vector2i;II)V", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), index = 1)
    private int preventVanillaClamping(int max) {
        // setting the minimum x value to be Integer.MIN_VALUE essentially preventing clamping
        return AdaptiveTooltipConfig.HANDLER.instance().preventVanillaClamping ? Integer.MIN_VALUE : max;
    }
}
