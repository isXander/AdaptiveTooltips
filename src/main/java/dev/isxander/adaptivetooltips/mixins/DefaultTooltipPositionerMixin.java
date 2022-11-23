package dev.isxander.adaptivetooltips.mixins;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.class_8001;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(class_8001.class)
public class DefaultTooltipPositionerMixin {
    @ModifyArg(method = "method_47945", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), index = 1)
    private int preventVanillaClamping(int max) {
        return AdaptiveTooltipConfig.INSTANCE.getConfig().preventVanillaClamping ? Integer.MIN_VALUE : max;
    }
}
